package arbolB.arbol;

public class ArbolB
{
    public NodoB raiz;
    public int clavesMin; //minimo de claves

    //Constructor
    public ArbolB(int grado) {
        this.clavesMin = (int) Math.ceil(grado / 2.0);
        this.raiz = new NodoB(clavesMin, true);
    }

    public void insertar(int clave) {
        NodoB r = raiz;

        if (r.clavesOcupadas == 2 * clavesMin - 1) {
            NodoB nuevaRaiz = new NodoB(clavesMin, false);
            nuevaRaiz.hijos[0] = r;
            raiz = nuevaRaiz;
            dividirHijo(nuevaRaiz, 0);
            insertarNoLleno(nuevaRaiz, clave);
        } else {
            insertarNoLleno(r, clave);
        }   
    }

    private void insertarNoLleno(NodoB nodo, int clave) {
        int i = nodo.clavesOcupadas - 1;

        if (nodo.esHoja) {
            while (i >= 0 && clave < nodo.claves[i]) {
                nodo.claves[i + 1] = nodo.claves[i];
                i--;
            }
            nodo.claves[i + 1] = clave;
            nodo.clavesOcupadas++;

        } else {
            while (i >= 0 && clave < nodo.claves[i]) {
                i--;
            }
            i++;

            if (nodo.hijos[i].clavesOcupadas == 2 * clavesMin - 1) {
                dividirHijo(nodo, i);
                if (clave > nodo.claves[i]) {
                    i++;
                }
            }
            insertarNoLleno(nodo.hijos[i], clave);
        }
    }

    private void dividirHijo(NodoB padre, int i) {
        NodoB y = padre.hijos[i];
        NodoB z = new NodoB(clavesMin, y.esHoja);
        z.clavesOcupadas = clavesMin - 1;

        for (int j = 0; j < clavesMin - 1; j++) {
            z.claves[j] = y.claves[j + clavesMin];
        }

        if (!y.esHoja) {
            for (int j = 0; j < clavesMin; j++) {
                z.hijos[j] = y.hijos[j + clavesMin];
            }
        }

        y.clavesOcupadas = clavesMin - 1;

        for (int j = padre.clavesOcupadas; j >= i + 1; j--) {
            padre.hijos[j + 1] = padre.hijos[j];
        }
        padre.hijos[i + 1] = z;

        for (int j = padre.clavesOcupadas - 1; j >= i; j--) {
            padre.claves[j + 1] = padre.claves[j];
        }

        padre.claves[i] = y.claves[clavesMin - 1];
        padre.clavesOcupadas++;
    }
}
