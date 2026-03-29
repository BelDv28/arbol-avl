package com.avl;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AVLTreeGUI extends JFrame {

    private final AVLTreePanel   treePanel;
    private final JTextArea      logArea;
    private final JTextField     fileField;
    private final JLabel         stepLabel;
    private final JButton        btnPrev;
    private final JButton        btnNext;
    private final JButton        btnPlay;

    // Historial de snapshots del árbol
    private final List<AVLNode>  snapshots = new ArrayList<>();
    private final List<String>   stepLogs  = new ArrayList<>();
    private int                  currentStep = 0;
    private Timer                playTimer;

    public AVLTreeGUI() {
        super("Árbol AVL — Programación III UMG");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 720);
        setLocationRelativeTo(null);

        // ── Panel superior ────────────────────────────────────────
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        fileField = new JTextField(30);
        fileField.setEditable(false);

        JButton btnBrowse = new JButton("📂 Archivo");
        JButton btnRun    = new JButton("▶ Generar AVL");
        JButton btnClear  = new JButton("🗑 Limpiar");

        btnBrowse.addActionListener(e -> seleccionarArchivo());
        btnRun   .addActionListener(e -> generarAVL());
        btnClear .addActionListener(e -> limpiar());

        top.add(new JLabel("Archivo:"));
        top.add(fileField);
        top.add(btnBrowse);
        top.add(btnRun);
        top.add(btnClear);

        // ── Panel gráfico ─────────────────────────────────────────
        treePanel = new AVLTreePanel();
        JScrollPane scrollGraf = new JScrollPane(treePanel);
        scrollGraf.setBorder(BorderFactory.createTitledBorder("Árbol AVL — Vista gráfica"));

        // ── Controles paso a paso ─────────────────────────────────
        JPanel stepPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        stepPanel.setBackground(new Color(17, 24, 39));

        btnPrev  = new JButton("◀ Anterior");
        btnNext  = new JButton("Siguiente ▶");
        btnPlay  = new JButton("⏵ Auto");
        stepLabel = new JLabel("Paso 0 / 0");
        stepLabel.setForeground(new Color(0, 212, 255));
        stepLabel.setFont(new Font("Monospaced", Font.BOLD, 13));

        btnPrev.setEnabled(false);
        btnNext.setEnabled(false);
        btnPlay.setEnabled(false);

        btnPrev.addActionListener(e -> mostrarPaso(currentStep - 1));
        btnNext.addActionListener(e -> mostrarPaso(currentStep + 1));
        btnPlay.addActionListener(e -> togglePlay());

        stepPanel.add(btnPrev);
        stepPanel.add(stepLabel);
        stepPanel.add(btnNext);
        stepPanel.add(btnPlay);

        // ── Bitácora ──────────────────────────────────────────────
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setBackground(Color.WHITE);
        logArea.setForeground(Color.BLACK);
        JScrollPane scrollLog = new JScrollPane(logArea);
        scrollLog.setBorder(BorderFactory.createTitledBorder("Bitácora de operaciones"));
        scrollLog.setPreferredSize(new Dimension(320, 0));

        // ── Layout ────────────────────────────────────────────────
        JPanel centerLeft = new JPanel(new BorderLayout());
        centerLeft.add(scrollGraf, BorderLayout.CENTER);
        centerLeft.add(stepPanel,  BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, centerLeft, scrollLog
        );
        split.setResizeWeight(0.72);

        add(top,   BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
    }

    // ── Seleccionar archivo ───────────────────────────────────────
    private void seleccionarArchivo() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleccionar archivo .txt");
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            fileField.setText(chooser.getSelectedFile().getAbsolutePath());
    }

    // ── Generar AVL ───────────────────────────────────────────────
    private void generarAVL() {
        String path = fileField.getText().trim();
        if (path.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Primero selecciona un archivo .txt",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String contenido;
        try {
            contenido = new String(new FileInputStream(path).readAllBytes());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo leer el archivo:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] partes = contenido.split("[,\\s]+");

        // Limpiar todo
        snapshots.clear();
        stepLogs.clear();
        logArea.setText("");
        treePanel.setTree(null);
        currentStep = 0;
        btnPrev.setEnabled(false);
        btnNext.setEnabled(false);
        btnPlay.setEnabled(false);
        stepLabel.setText("Procesando...");

        // ── SwingWorker ───────────────────────────────────────────
        SwingWorker<Void, String> worker = new SwingWorker<>() {

            @Override
            protected Void doInBackground() throws InterruptedException {
                StringBuilder logActual = new StringBuilder();

                AVLTree tree = new AVLTree(msg -> {
                    publish(msg);
                    logActual.append(msg).append("\n");
                });

                for (String parte : partes) {
                    String s = parte.trim();
                    if (s.isEmpty()) continue;
                    try {
                        logActual.setLength(0); // limpiar log de este paso
                        int num = Integer.parseInt(s);
                        tree.insert(num);

                        // Guardar snapshot del árbol tras insertar
                        snapshots.add(copyTree(tree.getRoot()));
                        stepLogs.add("Insertar: " + num + "\n\n" + logActual);

                        Thread.sleep(50);
                    } catch (NumberFormatException ex) {
                        publish("  ⚠ Valor inválido: " + s);
                    }
                }
                return null;
            }

            @Override
            protected void process(List<String> msgs) {
                for (String m : msgs) {
                    logArea.append(m + "\n");
                    logArea.setCaretPosition(logArea.getDocument().getLength());
                }
            }

            @Override
            protected void done() {
                if (!snapshots.isEmpty()) {
                    btnNext.setEnabled(true);
                    btnPlay.setEnabled(true);
                    mostrarPaso(0);
                    logArea.append("\n✅ " + snapshots.size() + " pasos listos. Usa ◀ ▶ para navegar.\n");
                }
            }
        };

        worker.execute();
    }

    // ── Mostrar un paso específico ────────────────────────────────
    private void mostrarPaso(int paso) {
        if (paso < 0 || paso >= snapshots.size()) return;
        currentStep = paso;

        treePanel.setTree(snapshots.get(paso));
        stepLabel.setText("Paso " + (paso + 1) + " / " + snapshots.size());

        // Mostrar log de este paso en la bitácora
        logArea.setText(stepLogs.get(paso));
        logArea.setCaretPosition(0);

        btnPrev.setEnabled(paso > 0);
        btnNext.setEnabled(paso < snapshots.size() - 1);
    }

    // ── Auto play ─────────────────────────────────────────────────
    private void togglePlay() {
        if (playTimer != null && playTimer.isRunning()) {
            playTimer.stop();
            btnPlay.setText("⏵ Auto");
            return;
        }
        btnPlay.setText("⏸ Pausar");
        playTimer = new Timer(800, e -> {
            if (currentStep < snapshots.size() - 1) {
                mostrarPaso(currentStep + 1);
            } else {
                playTimer.stop();
                btnPlay.setText("⏵ Auto");
            }
        });
        playTimer.start();
    }

    // ── Copiar árbol (snapshot) ───────────────────────────────────
    private AVLNode copyTree(AVLNode node) {
        if (node == null) return null;
        AVLNode copy  = new AVLNode(node.key);
        copy.height   = node.height;
        copy.left     = copyTree(node.left);
        copy.right    = copyTree(node.right);
        return copy;
    }

    // ── Limpiar ───────────────────────────────────────────────────
    private void limpiar() {
        if (playTimer != null) playTimer.stop();
        fileField.setText("");
        logArea.setText("");
        treePanel.setTree(null);
        snapshots.clear();
        stepLogs.clear();
        currentStep = 0;
        stepLabel.setText("Paso 0 / 0");
        btnPrev.setEnabled(false);
        btnNext.setEnabled(false);
        btnPlay.setEnabled(false);
        btnPlay.setText("⏵ Auto");
    }
}
