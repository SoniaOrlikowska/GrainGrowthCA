import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ButtonListeners {
    public static ColorGenerator canvas;

    public static void setColorGenerator(ColorGenerator value) {
        ButtonListeners.canvas = value;
    }

    public static class AddDualPhase implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }

    public static class ClearSpace implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            ColorGenerator.paintOnlyBorders();
            canvas.repaint();
        }
    }

    public static class AddBorders implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            ArrayList<Point> bordersCoordinates = PostInclusions.allBoundariesCoordinates(ColorGenerator.step1);
            ColorGenerator.setGrainBorderCoordinate(bordersCoordinates);
            canvas.repaint();
        }
    }

    public static class StartSimulation implements ActionListener {
        static HashMap<Integer, Point> selectedGrainsCoordinates = new HashMap<Integer, Point>();

        @Override
        public void actionPerformed(ActionEvent e) {
            JTextField numberOfGrainsText = GrainGrowthFront.getInstance().getNumberOfGrainsText();
            int numberOfGrains = Integer.parseInt(numberOfGrainsText.getText());
            int mainMatrixSizeX = GrainGrowthFront.getInstance().getxSizeSlider().getValue();
            int mainMatrixSizeY = GrainGrowthFront.getInstance().getySizeSlider().getValue();

            ColorGenerator.reset();
            ColorGenerator colorGenerator = new ColorGenerator(numberOfGrains, mainMatrixSizeX, mainMatrixSizeY, true);
            ButtonListeners.setColorGenerator(colorGenerator);

            if (GrainGrowthFront.showPanel.getComponents().length == 1) GrainGrowthFront.showPanel.remove(0);
            GrainGrowthFront.showPanel.add(colorGenerator);

            colorGenerator.setSize(800, 800);
            SaveCanvas.setCanvas(colorGenerator);
            colorGenerator.addMouseListener(new SelectedGrainsCoordinates());
        }
    }

    public static class SelectedGrainsCoordinates extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            int x = e.getX();
            int y = e.getY();
            int[][] step1 = ColorGenerator.getStep1();
            int sizeX = step1.length;
            int sizeY = step1[0].length;
            int p = 800 / sizeX;
            int q = 800 / sizeY;
            int key = step1[x / p][y / q];
            ArrayList<Point> grainBoundsCoordinates = new ArrayList<>();
            ArrayList<Point> dualPhaseCoordinates = new ArrayList<>();

            for (int i = 1; i < step1.length - 1; i++) {
                for (int j = 1; j < step1[0].length - 1; j++) {
                    if (GrainGrowthFront.getInstance().getDualPhaseRadio().isSelected()) {
                        System.out.println("dual phase select");
                        if (step1[i][j] == key) grainBoundsCoordinates.add(new Point(i, j));

                    } else if (GrainGrowthFront.getInstance().getSubstructureRadio().isSelected()) {
                        System.out.println("Bonjour substructure");
                        if (step1[i][j] == key) dualPhaseCoordinates.add(new Point(i, j));

                    } else if (step1[i][j] == key && (step1[i + 1][j + 1] != key || step1[i - 1][j - 1] != key || step1[i][j - 1] != key || step1[i][j + 1] != key)) {
                        System.out.println("hmmmm22222");
                        grainBoundsCoordinates.add(new Point(i, j));

                    }

                }
            }
            if (GrainGrowthFront.getInstance().getSubstructureRadio().isSelected()) {
                System.out.println(dualPhaseCoordinates);
                Substructure.setSubstructureCoordinates(dualPhaseCoordinates); // Get all coord for clicked grain
                int[][] sideStep = Substructure.subStateInitialMatrix(Substructure.findSubstructureSeeds()); // Create a matrix with new subgrains from clicked grain
                sideStep = Substructure.printSideStates(sideStep); // Create final state of the subgrain matrix
                int[][] newStep = Substructure.combineSubstructureMatrices(ColorGenerator.step1, sideStep); // combine "mother" matrix with submatrix

                ColorGenerator.setStep1(newStep); // Set step1 to be the new matrix with submatrix
            }

            ColorGenerator.setGrainBorderCoordinate(grainBoundsCoordinates);
            canvas.repaint();
        }

        public boolean isDualPhaseSelected() {
            boolean flag = false;
            if (GrainGrowthFront.getInstance().getDualPhaseRadio().isSelected()) flag = true;
            return flag;
        }
    }

    public static class DisableInclusions implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JTextField numberOfInclusions = GrainGrowthFront.getInstance().getInclusionsNumberText();
            JTextField sizeOfInclusions = GrainGrowthFront.getInstance().getInclusionSizeText();

            if (isInclusionSelected(e)) {
                numberOfInclusions.setEnabled(false);
                sizeOfInclusions.setEnabled(false);
                GrainGrowthFront.getInstance().getTimeOfInclusionsInsertComboBox().setEnabled(false);
            } else {
                numberOfInclusions.setEnabled(true);
                numberOfInclusions.setText("");
                GrainGrowthFront.getInstance().getInclusionSizeText().setEnabled(true);
                sizeOfInclusions.setText("");
                GrainGrowthFront.getInstance().getTimeOfInclusionsInsertComboBox().setEnabled(true);
            }
        }

        public boolean isInclusionSelected(ActionEvent e) {
            return e.getSource() instanceof JComboBox && GrainGrowthFront.getInstance().getTypeOfInclusionsComboBox().getSelectedIndex() == 0;
        }
    }

    public static class SaveToTxt implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            SaveToTextFile.printToFile();

            File output = new File("GrainGrowth.txt");
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.open(output);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static class SaveToBmp implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            SaveCanvas.saveCanvas();

            File output = new File("GrainGrowth.bmp");
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.open(output);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
