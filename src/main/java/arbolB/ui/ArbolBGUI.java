package arbolB.ui;

import arbolB.arbol.ArbolB;
import arbolB.arbol.LectorArchivo;
import arbolB.arbol.NodoB;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.List;

public class ArbolBGUI extends JFrame
{
    private JTextField  txtNumero;
    private JButton     btnInsertar;
    private JButton     btnCargarArchivo;
    private JButton     btnLimpiar;
    private JTree       jTree;
    private JTextArea   txtConsola;
    private JButton     btnSiguiente;
    private JButton     btnAnterior;
    private JButton     btnAutomatico;
    private JLabel      lblEstado;

    private ArbolB arbol;
    private static final int GRADO = 5;

    private List<Integer> listaNumerosArchivo;
    private int           indicePaso = 0;
    private boolean       modoActivo = false;

    public ArbolBGUI() {
        setTitle("Árbol B  —  Grado 5");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(740, 560);
        setLocationRelativeTo(null);
        reiniciarArbol();
        initComponents();
    }

    private void reiniciarArbol() {
        arbol = new ArbolB(GRADO);
    }
    private void initComponents() {

        JPanel panelRaiz = new JPanel(new BorderLayout(6, 6));
        panelRaiz.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel panelNorte = new JPanel(new BorderLayout(4, 4));

        txtNumero = new JTextField();
        txtNumero.setToolTipText("Ingrese un numero y presione Insertar o Enter");
        panelNorte.add(txtNumero, BorderLayout.CENTER);

        JPanel panelBotonesNorte = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        btnInsertar      = new JButton("Insertar");
        btnCargarArchivo = new JButton("Cargar Archivo");
        btnLimpiar       = new JButton("Limpiar");
        panelBotonesNorte.add(btnInsertar);
        panelBotonesNorte.add(btnCargarArchivo);
        panelBotonesNorte.add(btnLimpiar);
        panelNorte.add(panelBotonesNorte, BorderLayout.SOUTH);

        panelRaiz.add(panelNorte, BorderLayout.NORTH);

        jTree = new JTree(new DefaultMutableTreeNode("(árbol vacío)"));
        jTree.setShowsRootHandles(true);
        JScrollPane scrollTree = new JScrollPane(jTree);

        txtConsola = new JTextArea();
        txtConsola.setEditable(false);
        txtConsola.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        txtConsola.setLineWrap(true);
        JScrollPane scrollConsola = new JScrollPane(txtConsola);
        scrollConsola.setBorder(BorderFactory.createTitledBorder("Consola / Bitácora"));
        scrollConsola.setPreferredSize(new Dimension(210, 0));

        JSplitPane splitCentro = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, scrollTree, scrollConsola);
        splitCentro.setResizeWeight(0.70);
        splitCentro.setDividerLocation(480);
        panelRaiz.add(splitCentro, BorderLayout.CENTER);

        JPanel panelSur = new JPanel(new BorderLayout(4, 4));

        JPanel panelBotonesSur = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        btnAnterior   = new JButton("Anterior");
        btnSiguiente  = new JButton("Siguiente");
        btnAutomatico = new JButton("Automático");

        btnAnterior.setEnabled(false);
        btnSiguiente.setEnabled(false);
        btnAutomatico.setEnabled(false);

        panelBotonesSur.add(btnAnterior);
        panelBotonesSur.add(btnSiguiente);
        panelBotonesSur.add(btnAutomatico);

        lblEstado = new JLabel("Listo. Cargue un archivo o inserte un número.");
        lblEstado.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));

        panelSur.add(panelBotonesSur, BorderLayout.WEST);
        panelSur.add(lblEstado,       BorderLayout.CENTER);
        panelRaiz.add(panelSur, BorderLayout.SOUTH);

        add(panelRaiz);

        btnInsertar.addActionListener(e      -> accionInsertar());
        txtNumero.addActionListener(e        -> accionInsertar());
        btnCargarArchivo.addActionListener(e -> accionCargarArchivo());
        btnLimpiar.addActionListener(e       -> accionLimpiar());
        btnSiguiente.addActionListener(e     -> pasarSiguiente());
        btnAnterior.addActionListener(e      -> pasarAnterior());
        btnAutomatico.addActionListener(e    -> accionAutomatico());
    }
    private void accionLimpiar() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Desea limpiar el árbol y la consola?",
                "Confirmar limpieza",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        // Reiniciar árbol y estado de navegación
        reiniciarArbol();
        listaNumerosArchivo = null;
        indicePaso          = 0;
        modoActivo          = false;

        // Limpiar UI
        txtNumero.setText("");
        txtConsola.setText("");
        actualizarJTree();
        setBotonesNavegacion(false, false, false);
        setEstado("Listo. Cargue un archivo o inserte un número.");
        log("Árbol y consola limpiados.");
        txtNumero.requestFocus();
    }
    private void accionInsertar() {
        String texto = txtNumero.getText().trim();
        if (texto.isEmpty()) return;
        try {
            int clave = Integer.parseInt(texto);
            boolean insertado = arbol.insertar(clave);
            if (insertado) {
                actualizarJTree();
                log("Insertado: " + clave);
                setEstado("Insertado: " + clave);
            } else {
                log("DUPLICADO ignorado: " + clave + " ya existe en el árbol.");
                setEstado("Duplicado ignorado: " + clave);

            }
            txtNumero.setText("");
            txtNumero.requestFocus();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "\"" + texto + "\" no es un número entero válido.",
                    "Valor inválido", JOptionPane.WARNING_MESSAGE);
        }
    }
    private void accionCargarArchivo() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Seleccionar archivo de numeros");
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Archivos de texto (*.txt)", "txt"));

        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        String ruta   = fc.getSelectedFile().getAbsolutePath();
        String nombre = fc.getSelectedFile().getName();

        setBotonesCarga(false);
        setEstado("Leyendo archivo...");

        // Hilo background: solo lectura de disco
        SwingWorker<List<Integer>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Integer> doInBackground() throws Exception {
                return LectorArchivo.leer(ruta);
            }

            @Override
            protected void done() { // EDT: actualiza UI
                try {
                    listaNumerosArchivo = get();
                    log("──────────────────────────");
                    log("Archivo: " + nombre);
                    log("Números: " + listaNumerosArchivo.size());
                    preguntarModo();
                } catch (Exception ex) {
                    String msg = ex.getCause() != null
                            ? ex.getCause().getMessage() : ex.getMessage();
                    JOptionPane.showMessageDialog(ArbolBGUI.this,
                            "Error al leer el archivo:\n" + msg,
                            "Error", JOptionPane.ERROR_MESSAGE);
                    setEstado("Error al leer el archivo.");
                } finally {
                    setBotonesCarga(true);
                }
            }
        };
        worker.execute();
    }
    private void preguntarModo() {
        Object[] opciones = { "Paso a paso", "Automatico" };

        int eleccion = JOptionPane.showOptionDialog(
                this,
                "<html>Se cargaron <b>" + listaNumerosArchivo.size() + "</b> números.<br><br>"
                        + "<b>Paso a paso</b>: inserta clave por clave con Siguiente / Anterior.<br>"
                        + "<b>Automático</b>: inserta todas las claves y muestra el árbol final.</html>",
                "Modo de visualización",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]);

        // Reinicio del árbol en ambos modos
        reiniciarArbol();
        indicePaso = 0;
        modoActivo = false;
        actualizarJTree();

        if (eleccion == 1) {
            log("Modo: Automático");
            setEstado("Construyendo árbol...");
            setBotonesNavegacion(false, false, false);
            iniciarModoAutomatico();
        } else {
            log("Modo: Paso a paso");
            modoActivo = true;
            setBotonesNavegacion(false, true, true);
            setEstado("Paso a paso — 0 / " + listaNumerosArchivo.size()
                    + "  ·  Presione Siguiente");
        }
    }
    private void iniciarModoAutomatico() {
        SwingWorker<ArbolB, Void> worker = new SwingWorker<>() {
            @Override
            protected ArbolB doInBackground() {
                ArbolB arbolCompleto = new ArbolB(GRADO);
                for (int num : listaNumerosArchivo) {
                    arbolCompleto.insertar(num); // trabajo pesado en background
                }
                return arbolCompleto;
            }

            @Override
            protected void done() { // EDT — muestra resultado
                try {
                    arbol = get();
                    actualizarJTree();
                    for (int num : listaNumerosArchivo) {
                        log("  · " + num);
                    }
                    log("Árbol B completo — " + listaNumerosArchivo.size() + " claves.");
                    setEstado("Árbol B completo — "
                            + listaNumerosArchivo.size() + " claves insertadas.");
                } catch (Exception ex) {
                    log("Error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void pasarSiguiente() {
        if (!modoActivo || listaNumerosArchivo == null) return;
        if (indicePaso >= listaNumerosArchivo.size()) return;

        int clave = listaNumerosArchivo.get(indicePaso);
        arbol.insertar(clave);
        indicePaso++;
        actualizarJTree();
        log("[ " + indicePaso + " ]  Insertado: " + clave);
        setEstado("Paso " + indicePaso + " / " + listaNumerosArchivo.size()
                + "  —  clave: " + clave);

        btnAnterior.setEnabled(true);

        if (indicePaso >= listaNumerosArchivo.size()) {
            btnSiguiente.setEnabled(false);
            btnAutomatico.setEnabled(false);
            setEstado("Árbol B completo — "
                    + listaNumerosArchivo.size() + " claves insertadas.");
            log("Árbol B completo.");
        }
    }

    private void pasarAnterior() {
        if (!modoActivo || listaNumerosArchivo == null || indicePaso <= 0) return;

        indicePaso--;
        reiniciarArbol();
        for (int i = 0; i < indicePaso; i++) {
            arbol.insertar(listaNumerosArchivo.get(i));
        }
        actualizarJTree();

        btnSiguiente.setEnabled(true);
        btnAutomatico.setEnabled(true);
        btnAnterior.setEnabled(indicePaso > 0);

        if (indicePaso == 0) {
            log("Árbol reiniciado al inicio.");
            setEstado("Inicio — 0 / " + listaNumerosArchivo.size()
                    + "  ·  Presione Siguiente");
        } else {
            int ultima = listaNumerosArchivo.get(indicePaso - 1);
            log("[ " + indicePaso + " ]  Retroceso — última clave: " + ultima);
            setEstado("Paso " + indicePaso + " / " + listaNumerosArchivo.size()
                    + "  —  clave: " + ultima);
        }
    }

    private void accionAutomatico() {
        if (!modoActivo || listaNumerosArchivo == null) return;
        if (indicePaso >= listaNumerosArchivo.size()) return;

        while (indicePaso < listaNumerosArchivo.size()) {
            int clave = listaNumerosArchivo.get(indicePaso);
            arbol.insertar(clave);
            log("  · " + clave);
            indicePaso++;
        }
        actualizarJTree();
        setBotonesNavegacion(true, false, false);
        setEstado("Árbol B completo — "
                + listaNumerosArchivo.size() + " claves insertadas.");
        log("Árbol B completo.");
    }

    private void actualizarJTree() {
        DefaultMutableTreeNode raizNodo = construirNodo(arbol.raiz);
        jTree.setModel(new DefaultTreeModel(raizNodo));
        expandirTodo();
    }

    private DefaultMutableTreeNode construirNodo(NodoB nodo) {
        if (nodo == null)
            return new DefaultMutableTreeNode("(vacío)");

        String etiqueta = "[ " + clavesToString(nodo) + " ]";
        DefaultMutableTreeNode tn = new DefaultMutableTreeNode(etiqueta);

        if (!nodo.esHoja) {
            for (int i = 0; i <= nodo.clavesOcupadas; i++) {
                if (nodo.hijos[i] != null) {
                    tn.add(construirNodo(nodo.hijos[i]));
                }
            }
        }
        return tn;
    }

    private String clavesToString(NodoB nodo) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nodo.clavesOcupadas; i++) {
            if (i > 0) sb.append(", ");
            sb.append(nodo.claves[i]);
        }
        return sb.toString();
    }

    private void expandirTodo() {
        for (int i = 0; i < jTree.getRowCount(); i++) {
            jTree.expandRow(i);
        }
    }

    private void log(String msg) {
        txtConsola.append(msg + "\n");
        txtConsola.setCaretPosition(txtConsola.getDocument().getLength());
    }

    private void setEstado(String msg) {
        lblEstado.setText(msg);
    }

    private void setBotonesNavegacion(boolean anterior, boolean siguiente, boolean automatico) {
        btnAnterior.setEnabled(anterior);
        btnSiguiente.setEnabled(siguiente);
        btnAutomatico.setEnabled(automatico);
    }

    private void setBotonesCarga(boolean habilitado) {
        btnInsertar.setEnabled(habilitado);
        btnCargarArchivo.setEnabled(habilitado);
        btnLimpiar.setEnabled(habilitado);
    }

    // ── Main ──────────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new ArbolBGUI().setVisible(true);
        });
    }


}
