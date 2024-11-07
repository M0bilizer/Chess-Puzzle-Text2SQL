import json
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity


def main():
    with open('../resources/data/demonstrations_old.json', 'r') as file:
        data = json.load(file)
    categories = ['openai', 'gemini', 'llama']
    texts = {}

    for category in categories:
        for index, item in enumerate(data[category]):
            name = f"{category}-{index + 1}"
            texts[name] = item['text']

    keys = list(texts.keys())
    text_values = list(texts.values())

    # Vectorize the texts using TF-IDF
    vectorizer = TfidfVectorizer()
    tfidf_matrix = vectorizer.fit_transform(text_values)

    # Compute cosine similarity
    cosine_sim = cosine_similarity(tfidf_matrix)

    report_lines = []
    for i in range(len(keys)):
        for j in range(i + 1, len(keys)):
            similarity = cosine_sim[i][j]
            if similarity > 0.5:
                report_lines.append(f'### {similarity:.2f} | "{text_values[i]}" = "{text_values[j]}"')
                print(f'### {similarity:.2f} | "{text_values[i]}" = "{text_values[j]}"')
            else:
                report_lines.append(f'{similarity:.2f} | {text_values[i]}" = "{text_values[j]}"')

    with open('similarity_report_old.txt', 'w') as report_file:
        for line in report_lines:
            report_file.write(line + '\n')


if __name__ == "__main__":
    main()