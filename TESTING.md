# Testing the `/convert-measurements` Endpoint

The app exposes a single `GET` endpoint that converts a measurement string
into a list of integer totals, and records every request in the
`HISTORY_RECORD` table (browsable via the `/history` endpoints).

Base URL (when running locally or via Docker Compose, see `DOCKER.md`):
```
http://localhost:8080
```

## 1. Endpoint

```
GET /convert-measurements?input=<string>
```

Returns a JSON array of integers, e.g. `[1]`, `[2,2]`, or `[]`.

## 2. Quick test via browser

Just open in your browser (spaces must be URL-encoded as `%20`):

```
http://localhost:8080/convert-measurements?input=aa
http://localhost:8080/convert-measurements?input=ab%20ab
```

## 3. Quick test via curl

Use `curl -G --data-urlencode` so spaces/special characters are encoded
correctly:

```bash
curl -s -G "http://localhost:8080/convert-measurements" --data-urlencode "input=aa"
# -> [1]

curl -s -G "http://localhost:8080/convert-measurements" --data-urlencode "input=ab ab"
# -> [2,2]
```

## 4. Verified test cases (run against the live app)

Parsing rules recap: letters `a`-`z` carry values 1-26, `_` carries 0. A
package = one COUNT token followed by that many VALUE tokens; the package's
total is the sum of its values. A run of `z`s extends the next token's value
by `26 * (number of z's)`.

| Input      | Output     | Why                                                                 |
|------------|------------|---------------------------------------------------------------------|
| `aa`       | `[1]`      | count=`a`(1), 1 value `a`(1) → sum 1                                |
| `ab`       | `[2]`      | count=`a`(1), 1 value `b`(2) → sum 2                                |
| `abc`      | `[2]`      | first package sums to 2; `c`(3) starts a package needing 3 values but input ends → incomplete, stop with totals so far |
| `_a`       | `[0]`      | count=`_`(0) → empty package, total 0; `_` followed immediately by a letter stops processing |
| `_b`       | `[0]`      | same rule as above                                                  |
| `za`       | `[]`       | `z`+`a` → count = 26·1+1 = 27, needs 27 values but input ends → incomplete, no totals collected yet |
| `zza`      | `[]`       | `zz`+`a` → count = 26·2+1 = 53 → incomplete                        |
| `z_`       | `[]`       | `z`+`_` → count = 26·1+0 = 26 → incomplete                          |
| `ab ab`    | `[2,2]`    | two packages separated by a single space, each totalling 2          |
| `aab aaa`  | `[]`       | a space appears mid-token (after reading count `b`) → invalid input → empty list |
| `aa  bb`   | `[1]`      | first package totals 1; two consecutive spaces stop processing, returning totals collected so far |
| `ba`       | `[]`       | count=`b`(2) needs 2 values, only 1 available before input ends → incomplete |
| `ca_`      | `[]`       | count=`c`(3) needs 3 values, only 2 available → incomplete          |
| `1a`, `a1` | `[]`       | digits aren't valid characters (only `a`-`z`, `_`, space) → invalid input |

Run them all in one go:

```bash
for input in "aa" "ab" "abc" "_a" "za" "zza" "z_" "ab ab" "aab aaa" "aa  bb" "ba" "ca_"; do
  printf "input=[%s] -> " "$input"
  curl -s -G "http://localhost:8080/convert-measurements" --data-urlencode "input=$input"
  echo
done
```

## 5. Inspecting recorded history

Every call (valid or not) is persisted. Browse it via:

```bash
# list all recorded requests
curl -s http://localhost:8080/history | jq

# get one record by id
curl -s http://localhost:8080/history/1 | jq

# delete all history records
curl -s -X DELETE http://localhost:8080/history -o /dev/null -w "%{http_code}\n"
```

Example record shape:
```json
{
  "id": 1,
  "timestamp": "2026-06-08T15:49:32.774915",
  "sourceIpAddress": "0:0:0:0:0:0:0:1",
  "input": "2ab",
  "output": "[]"
}
```

You can also browse the `HISTORY_RECORD` table directly with DbVisualizer —
see `DATABASE_SETUP.md` for connection steps.

## 6. Postman / HTTP client

If you prefer a GUI client, import this as a request:
- Method: `GET`
- URL: `http://localhost:8080/convert-measurements`
- Query param: `input` = `aa` (or any of the test strings above)
