import json
import os

script_dir = os.path.dirname(__file__)
json_file_path = os.path.join(script_dir, '../misc/benchmark-result.json')
with open(json_file_path, 'r') as file:
    data = json.load(file)

full_total = 0
partial_total = 0
baseline_total = 0

for item in data:
    print(item['text'])
    full_total += int(item['full']['status'])
    partial_total += int(item['partial']['status'])
    baseline_total += int(item['baseline']['status'])

print(f"Full's total is {full_total}")
print(f"Partial's total is {partial_total}")
print(f"Baseline's total is {baseline_total}")