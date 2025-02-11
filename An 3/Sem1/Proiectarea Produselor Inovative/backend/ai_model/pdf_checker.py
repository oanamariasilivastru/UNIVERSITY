import os
import json
import pdfplumber
from model import MyModel
from sklearn.metrics.pairwise import cosine_similarity

def extract_text_from_pdf(pdf_path):
    """Extrage textul dintr-un fișier PDF."""
    try:
        with pdfplumber.open(pdf_path) as pdf:
            text = ''
            for page in pdf.pages:
                text += page.extract_text() or ''
        return text
    except Exception as e:
        print(f"Eroare la citirea PDF-ului: {e}")
        return ''

def calculate_similarity(pdf_text, sources):
    """Calculează similaritatea între textul PDF și sursele predefinite."""
    model = MyModel()

    pdf_embedding = model.encode(pdf_text)
    source_embeddings = model.encode(sources)

    similarities = cosine_similarity([pdf_embedding], source_embeddings)
    return similarities[0]

def load_sources(source_file):
    """Încarcă sursele dintr-un fișier JSON."""
    if not os.path.exists(source_file):
        print(f"Fișierul de surse {source_file} nu există.")
        return []
    try:
        with open(source_file, 'r', encoding='utf-8') as f:
            return json.load(f).get("sources", [])
    except Exception as e:
        print(f"Eroare la citirea fișierului de surse: {e}")
        return []

def save_results(results, output_file):
    """Salvează rezultatele într-un fișier JSON."""
    try:
        with open(output_file, 'w', encoding='utf-8') as f:
            json.dump(results, f, ensure_ascii=False, indent=4)
        print(f"Rezultatele au fost salvate în {output_file}.")
    except Exception as e:
        print(f"Eroare la salvarea rezultatelor: {e}")

def main():
    pdf_path = "document.pdf"
    if not os.path.exists(pdf_path):
        print(f"Fișierul {pdf_path} nu există.")
        return

    pdf_text = extract_text_from_pdf(pdf_path)
    if not pdf_text.strip():
        print("Nu s-a putut extrage text din PDF sau fișierul este gol.")
        return

    source_file = "sources.json"
    sources = load_sources(source_file)
    if not sources:
        print("Nu există surse pentru comparare.")
        return

    scores = calculate_similarity(pdf_text, sources)

    results = []
    for idx, score in enumerate(scores):
        results.append({
            "source": sources[idx],
            "similarity_score": round(score, 2)
        })

    results = sorted(results, key=lambda x: x["similarity_score"], reverse=True)

    print("\nRezultate similitudine:")
    for result in results:
        print(f"Sursă: {result['source']} - Scor Similaritate: {result['similarity_score']}")

    output_file = "results.json"
    save_results(results, output_file)

if __name__ == "__main__":
    main()
