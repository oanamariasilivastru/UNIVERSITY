import os
import pdfplumber
import torch
from sklearn.metrics.pairwise import cosine_similarity
from model import InferSent

MODEL_PATH = "infersent2.pkl"
EMBEDDING_PATH = "glove.840B.300d.txt"
V = 2

def load_infersent():
    params_model = {'bsize': 64, 'word_emb_dim': 300, 'enc_lstm_dim': 2048,
                    'pool_type': 'max', 'dpout_model': 0.0, 'version': V}
    model = InferSent(params_model)
    model.load_state_dict(torch.load(MODEL_PATH))
    model.set_w2v_path(EMBEDDING_PATH)
    model.build_vocab_k_words(K=100000)
    return model

def extract_text_from_pdf(pdf_path):
    try:
        with pdfplumber.open(pdf_path) as pdf:
            text = ''
            for page in pdf.pages:
                text += page.extract_text() or ''
        return text
    except Exception as e:
        print(f"Eroare la citirea PDF-ului: {e}")
        return ''


def find_contradictory_sources(pdf_text, sources, model):
    pdf_embedding = model.encode([pdf_text], tokenize=True)
    source_embeddings = model.encode(sources, tokenize=True)
    
    similarities = cosine_similarity(pdf_embedding, source_embeddings)[0]
    
    contradictory_sources = []
    for idx, score in enumerate(similarities):
        if score < 0.3:
            contradictory_sources.append({
                "source": sources[idx],
                "similarity_score": round(score, 2)
            })
    
    return contradictory_sources

def main():
    pdf_path = "document.pdf" 
    if not os.path.exists(pdf_path):
        print(f"Fișierul {pdf_path} nu există.")
        return

    pdf_text = extract_text_from_pdf(pdf_path)
    if not pdf_text.strip():
        print("Nu s-a putut extrage text din PDF sau fișierul este gol.")
        return

    sources = [
        "Hurricane Ian caused catastrophic flooding in Florida.",
        "Reports suggest Florida experienced less damage than expected.",
        "The volcanic eruption in Iceland was less dangerous than forecasted.",
        "Tokyo earthquake caused widespread structural damage."
    ]

    model = load_infersent()

    contradictory_sources = find_contradictory_sources(pdf_text, sources, model)

    if contradictory_sources:
        print("\nSurse contradictorii identificate:")
        for source in contradictory_sources:
            print(f"Sursă: {source['source']} - Scor Similaritate: {source['similarity_score']}")
    else:
        print("Nu au fost găsite surse contradictorii.")

if __name__ == "__main__":
    main()
