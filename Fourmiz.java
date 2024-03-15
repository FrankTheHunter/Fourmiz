import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

public class Fourmiz extends JFrame {
    private final int tailleGrille = 10;
    private final int tailleCellule = 40;
    private Point positionFourmi = new Point(0, 0);
    private final Point positionNourriture = new Point(9, 9);
    private final boolean[][] obstacles = new boolean[tailleGrille][tailleGrille];
    private List<Point> chemin = new ArrayList<>();

    public Fourmiz() {
        setTitle("Simulation de Déplacement des Fourmis avec A*");
        setSize(tailleGrille * tailleCellule + 50, tailleGrille * tailleCellule + 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panneau = new PanneauSimulation();
        add(panneau, BorderLayout.CENTER);
        JButton boutonDemarrer = new JButton("Démarrer la Simulation");
        add(boutonDemarrer, BorderLayout.SOUTH);

        boutonDemarrer.addActionListener(e -> {
            initObstacles(); // Générer de nouveaux obstacles aléatoires
            chemin = calculerCheminAStar(positionFourmi, positionNourriture);
            panneau.repaint();
        });

        setVisible(true);
    }

    private void initObstacles() {
        // Réinitialiser la grille d'obstacles
        for (int i = 0; i < tailleGrille; i++) {
            for (int j = 0; j < tailleGrille; j++) {
                obstacles[i][j] = false;
            }
        }

        // Générer des obstacles aléatoires
        Random rand = new Random();
        int nombreObstacles = 15; // Par exemple, générer 15 obstacles

        for (int i = 0; i < nombreObstacles; i++) {
            int x, y;
            do {
                x = rand.nextInt(tailleGrille);
                y = rand.nextInt(tailleGrille);
            } while ((x == positionFourmi.x && y == positionFourmi.y) || (x == positionNourriture.x && y == positionNourriture.y) || obstacles[x][y]);

            obstacles[x][y] = true;
        }
    }

    private List<Point> calculerCheminAStar(Point depart, Point arrivee) {
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(n -> n.f));
        Map<Point, Node> allNodes = new HashMap<>();

        Node start = new Node(depart, null, 0, distance(depart, arrivee));
        allNodes.put(start.position, start);
        openSet.add(start);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            if (current.position.equals(arrivee)) {
                return construireChemin(current);
            }

            for (int[] direction : new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}}) {
                Point point = new Point(current.position.x + direction[0], current.position.y + direction[1]);
                if (point.x < 0 || point.x >= tailleGrille || point.y < 0 || point.y >= tailleGrille || obstacles[point.x][point.y]) {
                    continue;
                }

                int tentativeG = current.g + 1;
                Node neighbor = allNodes.getOrDefault(point, new Node(point));
                allNodes.put(point, neighbor);

                if (tentativeG < neighbor.g) {
                    neighbor.parent = current;
                    neighbor.g = tentativeG;
                    neighbor.f = tentativeG + distance(neighbor.position, arrivee);

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        return Collections.emptyList();
    }

    private List<Point> construireChemin(Node current) {
        List<Point> path = new ArrayList<>();
        while (current != null) {
            path.add(0, current.position);
            current = current.parent;
        }
        return path;
    }

    private int distance(Point p1, Point p2) {
        return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
    }

    class Node {
        Point position;
        Node parent;
        int g; // Coût depuis le départ
        int f; // Coût total estimé

        Node(Point position, Node parent, int g, int h) {
            this.position = position;
            this.parent = parent;
            this.g = g;
            this.f = g + h;
        }

        Node(Point position) {
            this.position = position;
            this.g = Integer.MAX_VALUE;
            this.f = Integer.MAX_VALUE;
        }
    }

    class PanneauSimulation extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            for (int i = 0; i < tailleGrille; i++) {
                for (int j = 0; j < tailleGrille; j++) {
                    if (obstacles[i][j]) {
                        g.setColor(Color.BLACK);
                        g.fillRect(i * tailleCellule, j * tailleCellule, tailleCellule, tailleCellule);
                    }
                }
            }

            g.setColor(Color.RED);
            g.fillOval(positionFourmi.x * tailleCellule, positionFourmi.y * tailleCellule, tailleCellule, tailleCellule);

            g.setColor(Color.GREEN);
            g.fillOval(positionNourriture.x * tailleCellule, positionNourriture.y * tailleCellule, tailleCellule, tailleCellule);

            g.setColor(Color.BLUE);
            for(Point point : chemin) {
                g.fillOval(point.x * tailleCellule, point.y * tailleCellule, tailleCellule, tailleCellule);
            }
        }
    }

    public static void main(String[] args) {
        new Fourmiz();
    }
}
