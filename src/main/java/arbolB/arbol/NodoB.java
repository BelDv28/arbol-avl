package arbolB.arbol;

public class NodoB
{
    public int[] claves;
    public int clavesOcupadas;
    public NodoB[] hijos;
    public boolean esHoja;
    public int gradoMin;

    NodoB(int t, boolean isLeaf) {
        this.gradoMin      = t;
        this.esHoja = isLeaf;
        this.claves     = new int[2 * t - 1];   // máx. 4 slots
        this.hijos = new NodoB[2 * t];  // máx. 6 slots
        this.clavesOcupadas        = 0; // vacío al crear
    }
}
