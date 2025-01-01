import json
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

# Load the JSON data
with open('../backend/src/main/resources/data/demonstrations.json', 'r') as file:
    data = json.load(file)

# Extract texts from openai, gemini, and llama
categories = ['openai', 'gemini', 'llama']
texts = {}

for category in categories:
    for index, item in enumerate(data[category]):
        name = f"{category}-{index + 1}"
        texts[name] = item['text']

# Prepare to compare texts
keys = list(texts.keys())
text_values = list(texts.values())

# Vectorize the texts using TF-IDF
vectorizer = TfidfVectorizer()
tfidf_matrix = vectorizer.fit_transform(text_values)

# Compute cosine similarity
cosine_sim = cosine_similarity(tfidf_matrix)

# Prepare report lines
report_lines = []
for i in range(len(keys)):
    for j in range(i + 1, len(keys)):
        similarity = cosine_sim[i][j]
        report_lines.append(f"{keys[i]} = {keys[j]} : {similarity:.2f}")

# Write the report to a text file
with open('similarity_report.txt', 'w') as report_file:
    for line in report_lines:
        report_file.write(line + '\n')