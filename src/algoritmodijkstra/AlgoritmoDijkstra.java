package algoritmodijkstra;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Stack;
import javax.swing.*;
import javax.swing.event.*;

public class AlgoritmoDijkstra {

    public static JFrame panel_Inicio;

    public static void main(String[] args) {
        int Ancho = 700;
        int Alto = 540;
        panel_Inicio = new JFrame("Algoritmo Dijkstra");
        panel_Inicio.setContentPane(new Panel_Tablero(Ancho, Alto));
        panel_Inicio.pack();
        panel_Inicio.setResizable(false);

        Dimension Tamaño_Pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        double Ancho_Pantalla = Tamaño_Pantalla.getWidth();
        double Altura_Pantalla = Tamaño_Pantalla.getHeight();
        int x = ((int) Ancho_Pantalla - Ancho) / 2;
        int y = ((int) Altura_Pantalla - Alto) / 2;

        panel_Inicio.setLocation(x, y);
        panel_Inicio.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel_Inicio.setVisible(true);
    }

    public static class Panel_Tablero extends JPanel {

        private class Celdas {

            int Fila;
            int Columna;
            int Distancia;
            Celdas prev;

            public Celdas(int fila, int columna) {
                this.Fila = fila;
                this.Columna = columna;
            }
        }

        private class CompararCeldas_Distancias implements Comparator<Celdas> {

            @Override
            public int compare(Celdas cell1, Celdas cell2) {
                return cell1.Distancia - cell2.Distancia;
            }
        }

        private class MouseHandler implements MouseListener, MouseMotionListener {

            private int posicion_Fila, posicion_Columna, posicion_Evaluar;

            @Override
            public void mousePressed(MouseEvent evt) {
                int fila = (evt.getY() - 10) / tamaño_Cuadrado;
                int columna = (evt.getX() - 10) / tamaño_Cuadrado;
                if (evt.getButton() == MouseEvent.BUTTON3) { // Click Secundario 

                    if (fila >= 0 && fila < filas && columna >= 0 && columna < columnas) {

                        posicion_Fila = fila;
                        posicion_Columna = columna;
                        posicion_Evaluar = grid[fila][columna];

                        if (posicion_Evaluar == VACIO) {
                            grid[fila][columna] = OBSTACULO;
                        }
                        if (posicion_Evaluar == OBSTACULO) {
                            grid[fila][columna] = VACIO;
                        }
                    }

                } else {
                    // Click principal. Definir las pocisiones de inicio/fin
                    // TODO
                    if (iniciar_Algoritmo == null && posicion_Objetivo == null) {
                        iniciar_Algoritmo = new Celdas(fila, columna);//Se inicializa el punto de partida
                        grid[iniciar_Algoritmo.Fila][iniciar_Algoritmo.Columna] = ALGORITMO;
                        AbrirConjunto.add(iniciar_Algoritmo);
            
                    } else if (iniciar_Algoritmo != null && posicion_Objetivo == null) {
                        posicion_Objetivo = new Celdas(fila, columna);
                        grid[posicion_Objetivo.Fila][posicion_Objetivo.Columna] = OBJETIVO;
                        CerrarConjunto.removeAll(CerrarConjunto);
                    } else {
                        try {
                            grid[iniciar_Algoritmo.Fila][iniciar_Algoritmo.Columna] = VACIO;
                            grid[posicion_Objetivo.Fila][posicion_Objetivo.Columna] = VACIO;
                        } catch (IndexOutOfBoundsException e) {
                            // Al hacer mas pequeño, los indices no existen. Igrnorar.
                        }
                        iniciar_Algoritmo = null;
                        posicion_Objetivo = null;
                        AbrirConjunto.removeAll(AbrirConjunto);
                    }
                }
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent evt) {
                int row = (evt.getY() - 10) / tamaño_Cuadrado;
                int col = (evt.getX() - 10) / tamaño_Cuadrado;

                if (row >= 0 && row < filas && col >= 0 && col < columnas && !Buscando && !Encontrado) {
                    if ((row * columnas + col != posicion_Fila * columnas + posicion_Columna) && (posicion_Evaluar == ALGORITMO || posicion_Evaluar == OBJETIVO)) {
                        int new_val = grid[row][col];
                        if (new_val == VACIO) {
                            grid[row][col] = posicion_Evaluar;

                            if (posicion_Evaluar == ALGORITMO) {
                                iniciar_Algoritmo.Fila = row;
                                iniciar_Algoritmo.Columna = col;
                            } else {
                                posicion_Objetivo.Fila = row;
                                posicion_Objetivo.Columna = col;
                            }
                            grid[posicion_Fila][posicion_Columna] = new_val;
                            posicion_Fila = row;
                            posicion_Columna = col;
                            if (posicion_Evaluar == ALGORITMO) {
                                iniciar_Algoritmo.Fila = posicion_Fila;
                                iniciar_Algoritmo.Columna = posicion_Columna;
                            } else {
                                posicion_Objetivo.Fila = posicion_Fila;
                                posicion_Objetivo.Columna = posicion_Columna;
                            }
                            posicion_Evaluar = grid[row][col];
                        }
                    } else if (grid[row][col] != ALGORITMO && grid[row][col] != OBJETIVO) {
                        grid[row][col] = OBSTACULO;
                    }
                }
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
            }

            @Override
            public void mouseEntered(MouseEvent evt) {
            }

            @Override
            public void mouseExited(MouseEvent evt) {
            }

            @Override
            public void mouseMoved(MouseEvent evt) {
            }

            @Override
            public void mouseClicked(MouseEvent evt) {
            }
        }

        private class ActionHandler implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent evt) {
                String comando = evt.getActionCommand();
                if (comando.equals("Limpiar")) {
                    limpiar_Tablero();
                    diagonal.setEnabled(true);
                } else if (comando.equals("Paso a Paso") && !Encontrado && !finalDeBusqueda) {
                    if (!Buscando) {
                        try {
                            iniciar_Dijkstra();
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(Panel_Tablero.this,
                                    e.getMessage(),
                                    "Problem", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                    Buscando = true;
                    diagonal.setEnabled(false);
                    temporizador.stop();

                    if ((Grafo.isEmpty()) || (AbrirConjunto.isEmpty())) {
                        finalDeBusqueda = true;
                        grid[iniciar_Algoritmo.Fila][iniciar_Algoritmo.Columna] = ALGORITMO;
                    } else {
                        expandir_Busqueda();
                        if (Encontrado) {
                            trazarRuta();
                        }
                    }
                    repaint();
                } else if (comando.equals("Animación") && !finalDeBusqueda) {
                    if (!Buscando) {
                        try {
                            iniciar_Dijkstra();
                        } catch (Exception e) {
                            if (!(e instanceof NullPointerException)) {
                                JOptionPane.showMessageDialog(Panel_Tablero.this,
                                        e.getMessage(),
                                        "Problem", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        }
                    }
                    Buscando = true;

                    diagonal.setEnabled(false);
                    temporizador.setDelay(delay);
                    temporizador.start();
                }
            }
        }

        private class RepaintAction implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent evt) {
                if ((Grafo.isEmpty()) || (AbrirConjunto.isEmpty())) {
                    finalDeBusqueda = true;
                    grid[iniciar_Algoritmo.Fila][iniciar_Algoritmo.Columna] = ALGORITMO;
                } else {
                    expandir_Busqueda();
                    if (Encontrado) {
                        temporizador.stop();
                        finalDeBusqueda = true;
                        trazarRuta();
                    }
                }
                repaint();
            }
        }

        private final static int INFINITY = Integer.MAX_VALUE,
                VACIO = 0,
                OBSTACULO = 1,
                ALGORITMO = 2,
                OBJETIVO = 3,
                FRONTIER = 4,
                CLOSED = 5,
                ROUTE = 6;

        JTextField rowsField, columnsField, coordenadaFilas_X;

        int filas = 15, columnas = 15,
                tamaño_Cuadrado = 500 / filas;

        ArrayList<Celdas> AbrirConjunto = new ArrayList();
        ArrayList<Celdas> CerrarConjunto = new ArrayList();
        ArrayList<Celdas> Grafo = new ArrayList();

        Celdas iniciar_Algoritmo;//Celda de inicio
        Celdas posicion_Objetivo;//Celda de fin

        JSlider slider;

        JCheckBox diagonal;

        int[][] grid;
        boolean Encontrado;
        boolean Buscando;
        boolean finalDeBusqueda;
        int delay;
        int expansion;

        RepaintAction action = new RepaintAction();

        Timer temporizador;

        public Panel_Tablero(int width, int height) {

            setLayout(null);

            MouseHandler listener = new MouseHandler();
            addMouseListener(listener);
            addMouseMotionListener(listener);

            setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK));
            setPreferredSize(new Dimension(width, height));

            grid = new int[filas][columnas];

            JLabel rowsLbl = new JLabel("N° de Filas:", JLabel.RIGHT);
            rowsLbl.setFont(new Font("Helvetica", Font.PLAIN, 13));

            rowsField = new JTextField();
            rowsField.setText(Integer.toString(filas));

            JLabel columnsLbl = new JLabel("N° de Columnas:", JLabel.RIGHT);
            columnsLbl.setFont(new Font("Helvetica", Font.PLAIN, 13));

            columnsField = new JTextField();
            columnsField.setText(Integer.toString(columnas));

            JLabel inicioFilasCasilla = new JLabel("Coordenada Inicio: ", JLabel.LEFT);
            inicioFilasCasilla.setFont(new Font("Helvetica", Font.PLAIN, 13));

            JLabel inicioFilasCasillaX = new JLabel("X:", JLabel.LEFT);
            inicioFilasCasillaX.setFont(new Font("Helvetica", Font.PLAIN, 13));

            coordenadaFilas_X = new JTextField();
            coordenadaFilas_X.setText(Integer.toString(filas));

            JLabel inicioFilasCasillaY = new JLabel("Y:", JLabel.LEFT);
            inicioFilasCasillaY.setFont(new Font("Helvetica", Font.PLAIN, 13));

            JButton resetButton = new JButton("Nuevo Tablero");
            resetButton.addActionListener(new ActionHandler());
            resetButton.setBackground(Color.lightGray);
            resetButton.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    Accion_limpiarTablero(evt);
                }
            });

            JButton clearButton = new JButton("Limpiar");
            clearButton.addActionListener(new ActionHandler());
            clearButton.setBackground(Color.lightGray);

            JButton stepButton = new JButton("Paso a Paso");
            stepButton.addActionListener(new ActionHandler());
            stepButton.setBackground(Color.lightGray);

            JButton animationButton = new JButton("Animación");
            animationButton.addActionListener(new ActionHandler());
            animationButton.setBackground(Color.lightGray);

            JLabel velocity = new JLabel("Velocidad", JLabel.CENTER);
            velocity.setFont(new Font("Helvetica", Font.PLAIN, 10));

            slider = new JSlider(0, 1000, 500); // initial value of delay 500 msec

            delay = 1000 - slider.getValue();
            slider.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent evt) {
                    JSlider source = (JSlider) evt.getSource();
                    if (!source.getValueIsAdjusting()) {
                        delay = 1000 - source.getValue();
                    }
                }
            });

            JPanel algoPanel = new JPanel();

            diagonal = new JCheckBox("Movimientos Diagonales");

            add(rowsLbl);
            add(rowsField);
            add(columnsLbl);
            add(columnsField);
            add(resetButton);
            add(inicioFilasCasilla);
            add(inicioFilasCasillaX);
            add(inicioFilasCasillaY);
            add(clearButton);
            add(stepButton);
            add(animationButton);
            add(velocity);
            add(slider);
            add(algoPanel);
            add(diagonal);

            rowsLbl.setBounds(520, 5, 140, 25);
            rowsField.setBounds(665, 5, 25, 25);
            columnsLbl.setBounds(520, 35, 140, 25);
            columnsField.setBounds(665, 35, 25, 25);
            resetButton.setBounds(520, 65, 170, 25);
            clearButton.setBounds(520, 95, 170, 25);
            stepButton.setBounds(520, 125, 170, 25);
            animationButton.setBounds(520, 155, 170, 25);
            inicioFilasCasilla.setBounds(520, 230, 170, 25);
            inicioFilasCasillaX.setBounds(520, 250, 170, 25);
            inicioFilasCasillaY.setBounds(600, 250, 170, 25);
            velocity.setBounds(520, 450, 170, 10);
            slider.setBounds(515, 465, 170, 25);
            algoPanel.setLocation(520, 250);
            algoPanel.setSize(170, 100);
            diagonal.setBounds(520, 490, 170, 25);

            temporizador = new Timer(delay, action);

            limpiar_Tablero();
        }

        private void Accion_limpiarTablero(java.awt.event.ActionEvent evt) {
            inicializarTablero(false);
        }
        
        private void inicializarTablero(Boolean crearTablero) {
            int filaVieja = filas;
            int columnaVieja = columnas;
            try {
                if (!rowsField.getText().isEmpty()) {
                    filas = Integer.parseInt(rowsField.getText());
                } else {
                    JOptionPane.showMessageDialog(this,"Para el N° de filas, esta tabla solo acepta numeros entre el 5 y 83", "Hay un problema", JOptionPane.ERROR_MESSAGE);
                    filas = filaVieja;
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Para el N° de columnas, solo acepta la tabla numeros entre el 5 y el 83","Hay un problema", JOptionPane.ERROR_MESSAGE);
                filas = filaVieja;
                return;
            }
            if (filas < 5 || filas > 83) {
                JOptionPane.showMessageDialog(this,
                        "Para el N° de filas, esta tabla solo acepta numeros entre el 5 y 83","Hay un problema", JOptionPane.ERROR_MESSAGE);
                filas = filaVieja;
                return;
            }
            try {
                if (!columnsField.getText().isEmpty()) {
                    columnas = Integer.parseInt(columnsField.getText());
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Para el N° de columnas, solo acepta la tabla numeros entre el 5 y el 83","Hay un problema", JOptionPane.ERROR_MESSAGE);
                    columnas = columnaVieja;
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Para el N° de columnas, solo acepta la tabla numeros entre el 5 y el 83","Hay un problema",JOptionPane.ERROR_MESSAGE);
                columnas = columnaVieja;
                return;
            }
            if (columnas < 5 || columnas > 83) {
                JOptionPane.showMessageDialog(this,
                        "Para el N° de columnas, solo acepta la tabla numeros entre el 5 y el 83","Hay un problema", JOptionPane.ERROR_MESSAGE);
                columnas = columnaVieja;
                return;
            }
            tamaño_Cuadrado = 500 / (filas > columnas ? filas : columnas);
            if (crearTablero && filas % 2 == 0) {
                filas -= 1;
            }
            if (crearTablero && columnas % 2 == 0) {
                columnas -= 1;
            }
            grid = new int[filas][columnas];
            diagonal.setSelected(false);
            diagonal.setEnabled(true);
            slider.setValue(500);
            limpiar_Tablero();
        }

        private void expandir_Busqueda() {
            Celdas expandir;
            if (Grafo.isEmpty()) {
                return;
            }
            expandir = Grafo.remove(0);
            CerrarConjunto.add(expandir);
            if (expandir.Fila == posicion_Objetivo.Fila && expandir.Columna == posicion_Objetivo.Columna) {
                Encontrado = true;
                return;
            }
            expansion++;
            grid[expandir.Fila][expandir.Columna] = CLOSED;

            if (expandir.Distancia == INFINITY) {
                return;
            }
            ArrayList<Celdas> neighbors = Nodos_Sucesores(expandir, false);
            for (Celdas v : neighbors) {
                int alt = expandir.Distancia + medir_Distancias(expandir, v);
                if (alt < v.Distancia) {
                    v.Distancia = alt;
                    v.prev = expandir;
                    grid[v.Fila][v.Columna] = FRONTIER;
                    Collections.sort(Grafo, new CompararCeldas_Distancias());
                }
            }
        }

        private ArrayList<Celdas> Nodos_Sucesores(Celdas current, boolean makeConnected) {
            int r = current.Fila;
            int c = current.Columna;
            ArrayList<Celdas> temp = new ArrayList<>();
            if (r > 0 && grid[r - 1][c] != OBSTACULO
                    && (Nodos_En_Lista(AbrirConjunto, new Celdas(r - 1, c)) == -1
                    && Nodos_En_Lista(CerrarConjunto, new Celdas(r - 1, c)) == -1)) {
                Celdas cell = new Celdas(r - 1, c);

                if (makeConnected) {
                    temp.add(cell);
                } else {
                    int graphIndex = Nodos_En_Lista(Grafo, cell);
                    if (graphIndex > -1) {
                        temp.add(Grafo.get(graphIndex));
                    }
                }

            }
            if (diagonal.isSelected()) {
                if (r > 0 && c < columnas - 1 && grid[r - 1][c + 1] != OBSTACULO && (grid[r - 1][c] != OBSTACULO || grid[r][c + 1] != OBSTACULO) && (Nodos_En_Lista(AbrirConjunto, new Celdas(r - 1, c + 1)) == -1 && Nodos_En_Lista(CerrarConjunto, new Celdas(r - 1, c + 1)) == -1)) {
                    Celdas cell = new Celdas(r - 1, c + 1);

                    if (makeConnected) {
                        temp.add(cell);
                    } else {
                        int graphIndex = Nodos_En_Lista(Grafo, cell);
                        if (graphIndex > -1) {
                            temp.add(Grafo.get(graphIndex));
                        }
                    }
                }
            }
            if (c < columnas - 1 && grid[r][c + 1] != OBSTACULO && (Nodos_En_Lista(AbrirConjunto, new Celdas(r, c + 1)) == -1 && Nodos_En_Lista(CerrarConjunto, new Celdas(r, c + 1)) == -1)) {
                Celdas cell = new Celdas(r, c + 1);

                if (makeConnected) {
                    temp.add(cell);
                } else {
                    int graphIndex = Nodos_En_Lista(Grafo, cell);
                    if (graphIndex > -1) {
                        temp.add(Grafo.get(graphIndex));
                    }
                }
            }
            if (diagonal.isSelected()) {
                if (r < filas - 1 && c < columnas - 1 && grid[r + 1][c + 1] != OBSTACULO
                        && (grid[r + 1][c] != OBSTACULO || grid[r][c + 1] != OBSTACULO)
                        && (Nodos_En_Lista(AbrirConjunto, new Celdas(r + 1, c + 1)) == -1
                        && Nodos_En_Lista(CerrarConjunto, new Celdas(r + 1, c + 1)) == -1)) {
                    Celdas cell = new Celdas(r + 1, c + 1);

                    if (makeConnected) {
                        temp.add(cell);
                    } else {
                        int graphIndex = Nodos_En_Lista(Grafo, cell);
                        if (graphIndex > -1) {
                            temp.add(Grafo.get(graphIndex));
                        }
                    }
                }
            }
            if (r < filas - 1 && grid[r + 1][c] != OBSTACULO && (Nodos_En_Lista(AbrirConjunto, new Celdas(r + 1, c)) == -1 && Nodos_En_Lista(CerrarConjunto, new Celdas(r + 1, c)) == -1)) {
                Celdas cell = new Celdas(r + 1, c);

                if (makeConnected) {
                    temp.add(cell);
                } else {
                    int graphIndex = Nodos_En_Lista(Grafo, cell);
                    if (graphIndex > -1) {
                        temp.add(Grafo.get(graphIndex));
                    }
                }
            }
            if (diagonal.isSelected()) {
                if (r < filas - 1 && c > 0 && grid[r + 1][c - 1] != OBSTACULO
                        && (grid[r + 1][c] != OBSTACULO || grid[r][c - 1] != OBSTACULO)
                        && (Nodos_En_Lista(AbrirConjunto, new Celdas(r + 1, c - 1)) == -1
                        && Nodos_En_Lista(CerrarConjunto, new Celdas(r + 1, c - 1)) == -1)) {
                    Celdas cell = new Celdas(r + 1, c - 1);

                    if (makeConnected) {
                        temp.add(cell);
                    } else {
                        int graphIndex = Nodos_En_Lista(Grafo, cell);
                        if (graphIndex > -1) {
                            temp.add(Grafo.get(graphIndex));
                        }
                    }
                }
            }
            if (c > 0 && grid[r][c - 1] != OBSTACULO
                    && (Nodos_En_Lista(AbrirConjunto, new Celdas(r, c - 1)) == -1
                    && Nodos_En_Lista(CerrarConjunto, new Celdas(r, c - 1)) == -1)) {
                Celdas cell = new Celdas(r, c - 1);

                if (makeConnected) {
                    temp.add(cell);
                } else {
                    int graphIndex = Nodos_En_Lista(Grafo, cell);
                    if (graphIndex > -1) {
                        temp.add(Grafo.get(graphIndex));
                    }
                }
            }
            if (diagonal.isSelected()) {
                if (r > 0 && c > 0 && grid[r - 1][c - 1] != OBSTACULO
                        && (grid[r - 1][c] != OBSTACULO || grid[r][c - 1] != OBSTACULO)
                        && (Nodos_En_Lista(AbrirConjunto, new Celdas(r - 1, c - 1)) == -1
                        && Nodos_En_Lista(CerrarConjunto, new Celdas(r - 1, c - 1)) == -1)) {
                    Celdas cell = new Celdas(r - 1, c - 1);

                    if (makeConnected) {
                        temp.add(cell);
                    } else {
                        int graphIndex = Nodos_En_Lista(Grafo, cell);
                        if (graphIndex > -1) {
                            temp.add(Grafo.get(graphIndex));
                        }
                    }
                }
            }
            return temp;
        }

        private int Nodos_En_Lista(ArrayList<Celdas> list, Celdas current) {
            int index = -1;
            for (int i = 0; i < list.size(); i++) {
                if (current.Fila == list.get(i).Fila && current.Columna == list.get(i).Columna) {
                    index = i;
                    break;
                }
            }
            return index;
        }

        private int medir_Distancias(Celdas u, Celdas v) {
            int distancia;
            int distancia_X = u.Columna - v.Columna;
            int distancia_Y = u.Fila - v.Fila;
            if (diagonal.isSelected()) {
                distancia = (int) ((double) 1000 * Math.sqrt(distancia_X * distancia_X + distancia_Y * distancia_Y));
            } else {
                distancia = Math.abs(distancia_X) + Math.abs(distancia_Y);
            }
            return distancia;
        }

        private void trazarRuta() {
            Buscando = false;
            finalDeBusqueda = true;
            int steps = 0;
            double distance = 0;
            int index = Nodos_En_Lista(CerrarConjunto, posicion_Objetivo);
            Celdas cur = CerrarConjunto.get(index);
            grid[cur.Fila][cur.Columna] = OBJETIVO;
            do {
                steps++;
                if (diagonal.isSelected()) {
                    int dx = cur.Columna - cur.prev.Columna;
                    int dy = cur.Fila - cur.prev.Fila;
                    distance += Math.sqrt(dx * dx + dy * dy);
                } else {
                    distance++;
                }
                cur = cur.prev;
                grid[cur.Fila][cur.Columna] = ROUTE;
            } while (!(cur.Fila == iniciar_Algoritmo.Fila && cur.Columna == iniciar_Algoritmo.Columna));
            grid[iniciar_Algoritmo.Fila][iniciar_Algoritmo.Columna] = ALGORITMO;
        }

        private void limpiar_Tablero() {
            if (Buscando || finalDeBusqueda) {
                for (int r = 0; r < filas; r++) {
                    for (int c = 0; c < columnas; c++) {
                        if (grid[r][c] == FRONTIER || grid[r][c] == CLOSED || grid[r][c] == ROUTE) {
                            grid[r][c] = VACIO;
                        }
                        if (grid[r][c] == ALGORITMO) {
                            iniciar_Algoritmo = new Celdas(r, c);
                        }
                        if (grid[r][c] == OBJETIVO) {
                            posicion_Objetivo = new Celdas(r, c);
                        }
                    }
                }
                Buscando = false;
            } else {
                for (int r = 0; r < filas; r++) {
                    for (int c = 0; c < columnas; c++) {
                        grid[r][c] = VACIO;
                    }
                }
            }

            expansion = 0;
            Encontrado = false;
            Buscando = false;
            finalDeBusqueda = false;
            AbrirConjunto.removeAll(AbrirConjunto);            
            temporizador.stop();
            repaint();
        }

        private void encontrar_Componentes_Encontrados(Celdas v) {
            Stack<Celdas> stack;
            stack = new Stack();
            ArrayList<Celdas> succesors;
            stack.push(v);
            Grafo.add(v);
            while (!stack.isEmpty()) {
                v = stack.pop();
                succesors = Nodos_Sucesores(v, true);
                for (Celdas c : succesors) {
                    if (Nodos_En_Lista(Grafo, c) == -1) {
                        stack.push(c);
                        Grafo.add(c);
                    }
                }
            }
        }

        private void iniciar_Dijkstra() throws Exception {
            if (iniciar_Algoritmo == null) {
                throw new Exception("The starting point isn't defined. Click a starting point.");
            }
            if (posicion_Objetivo == null) {
                throw new Exception("The ending point isn't defined. Click a ending point.");
            }
            Grafo.removeAll(Grafo);
            encontrar_Componentes_Encontrados(iniciar_Algoritmo);
            for (Celdas v : Grafo) {
                v.Distancia = INFINITY;
                v.prev = null;
            }
            Grafo.get(Nodos_En_Lista(Grafo, iniciar_Algoritmo)).Distancia = 0;

            Collections.sort(Grafo, new CompararCeldas_Distancias());

            CerrarConjunto.removeAll(CerrarConjunto);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.setColor(Color.DARK_GRAY);
            g.fillRect(10, 10, columnas * tamaño_Cuadrado + 1, filas * tamaño_Cuadrado + 1);

            for (int r = 0; r < filas; r++) {
                for (int c = 0; c < columnas; c++) {
                    if (grid[r][c] == VACIO) {
                        g.setColor(Color.WHITE);
                    } else if (grid[r][c] == ALGORITMO) {
                        g.setColor(Color.ORANGE);
                    } else if (grid[r][c] == OBJETIVO) {
                        g.setColor(Color.GREEN);
                    } else if (grid[r][c] == OBSTACULO) {
                        g.setColor(Color.BLACK);
                    } else if (grid[r][c] == FRONTIER) {
                        g.setColor(Color.RED);
                    } else if (grid[r][c] == CLOSED) {
                        g.setColor(Color.gray);
                    } else if (grid[r][c] == ROUTE) {
                        g.setColor(Color.cyan);
                    }
                    g.fillRect(11 + c * tamaño_Cuadrado, 11 + r * tamaño_Cuadrado, tamaño_Cuadrado - 1, tamaño_Cuadrado - 1);
                }
            }
        }
    }
}
