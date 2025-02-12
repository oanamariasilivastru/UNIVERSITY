// VAR CU A
class Gramatica:
    def __init__(self):
        self.terminale = set()
        self.neterminale = set()
        self.start = None
        self.productii = dict()

    def incarca_din_fisier(self, nume_fisier):
        with open(nume_fisier, 'r') as fisier:
            for linie in fisier:
                linie = linie.strip()
                if not linie or linie.startswith('#'):
                    continue 
                if '->' not in linie:
                    raise ValueError(f"Linie invalida in fisier: {linie}")

                parte_stanga, parte_dreapta = linie.split('->')
                parte_stanga = parte_stanga.strip()
                parte_dreapta = parte_dreapta.strip()

                neterminal = parte_stanga
                if len(neterminal) != 1 or not neterminal.isupper():
                    raise ValueError(f"Neterminal invalid: {neterminal}")

                self.neterminale.add(neterminal)
                if not self.start:
                    self.start = neterminal

                expansiuni = [exp.strip() for exp in parte_dreapta.split('|')]
                self.productii.setdefault(neterminal, []).extend(expansiuni)

                for exp in expansiuni:
                    for simbol in exp:
                        if simbol.islower():
                            self.terminale.add(simbol)
                        elif simbol.isupper():
                            self.neterminale.add(simbol)
                        else:
                            raise ValueError(f"Simbol invalid: {simbol}")

    def afiseaza_productii_recursive(self):
        reguli_recursive = {}
        for neterminal, expansiuni in self.productii.items():
            for exp in expansiuni:
                if neterminal in exp:
                    reguli_recursive.setdefault(neterminal, []).append(exp)

        if not reguli_recursive:
            print("Nu exista reguli de productie recursive.")
            return

        print("Reguli de productie recursive:")
        for neterminal, expansiuni in reguli_recursive.items():
            expansiuni_formatate = " | ".join(expansiuni)
            print(f"{neterminal} -> {expansiuni_formatate}")

    def afiseaza_gramatica(self):
        print(f"Simbol de start: {self.start}")
        print(f"Neterminale: {', '.join(sorted(self.neterminale))}")
        print(f"Terminale: {', '.join(sorted(self.terminale))}")
        print("Reguli de productie:")
        for neterminal, expansiuni in self.productii.items():
            expansiuni_formatate = " | ".join(expansiuni)
            print(f"  {neterminal} -> {expansiuni_formatate}")

def main():
    nume_fisier = 'gramatica.txt'
    gramatica = Gramatica()

    try:
        gramatica.incarca_din_fisier(nume_fisier)
    except ValueError as e:
        print(f"Eroare la incarcarea gramaticii: {e}")
        return

    print("Gramatica incarcata:")
    gramatica.afiseaza_gramatica()
    print("\n")

    gramatica.afiseaza_productii_recursive()

if __name__ == "__main__":
    main()
