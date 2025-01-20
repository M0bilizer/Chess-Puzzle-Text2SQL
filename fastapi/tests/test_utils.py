from src.utils import ngrams, tokenize, jaccard_similarity


def test_ngrams():
    input_text = "This is a test"
    expected_output = ["This", "is", "a", "test"]
    assert ngrams(input_text, 1) == expected_output

    expected_output = ["This is", "is a", "a test"]
    assert ngrams(input_text, 2) == expected_output

    expected_output = ["This is a", "is a test"]
    assert ngrams(input_text, 3) == expected_output

    expected_output = ["This is a test"]
    assert ngrams(input_text, 4) == expected_output


def test_tokenize():
    input_text = "This is a test"
    expected_output = {"this", "is", "a", "test"}
    assert tokenize(input_text) == expected_output

    input_text = "This is a TEST, with punctuation!"
    expected_output = {"this", "is", "a", "test", "with", "punctuation"}
    assert tokenize(input_text) == expected_output


def test_jaccard_similarity():
    set1 = {"this", "is", "a", "test"}
    set2 = {"this", "is", "a", "test"}
    assert jaccard_similarity(set1, set2) == 1.0

    set1 = {"this", "is", "a", "test"}
    set2 = {"this", "is", "another", "test"}
    assert jaccard_similarity(set1, set2) == 0.6

    set1 = {"this", "is", "a", "test"}
    set2 = {"completely", "different", "words"}
    assert jaccard_similarity(set1, set2) == 0.0
