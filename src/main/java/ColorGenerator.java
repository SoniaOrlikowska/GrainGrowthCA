import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class ColorGenerator extends Canvas {
    int numberOfGrains;
    int mainMatrixSizeX;
    int mainMatrixSizeY;

    public ColorGenerator(int numberOfGrains, int mainMatrixSizeX, int mainMatrixSizeY) {
        this.numberOfGrains = numberOfGrains;
        this.mainMatrixSizeX = mainMatrixSizeX;
        this.mainMatrixSizeY = mainMatrixSizeY;
    }

    public void paint(Graphics g) {
        super.paint(g);

        GrainGrowthFront grainGrowthFront = GrainGrowthFront.getInstance();
        GrainGrowth gg = new GrainGrowth();
        int matrixSizeX = grainGrowthFront.getxSizeSlider().getValue();
        int matrixSizeY = grainGrowthFront.getySizeSlider().getValue();
        int grainNumber = Integer.parseInt(grainGrowthFront.getNumberOfGrainsText().getText());
        int inclusionSize = Integer.parseInt(grainGrowthFront.getInclusionSizeText().getText());
        HashMap<Integer, Color> colorMap = distinctColoursGenerator(numberOfGrains);
        int[][] step0 = new int[matrixSizeX][matrixSizeY];
       // if (isNoInclusions()) {
            step0 = InitialStateGenerator.generateInitial(matrixSizeX, matrixSizeY, grainNumber);//}
        if (isSquarePrior()) {
            step0 = SquareInclusionsGenerator.generateRandomInclusionsStartCoordinates(inclusionSize, matrixSizeX,matrixSizeY,2 );}

        printAllStates(step0, colorMap, g);

    }

    public boolean isSquarePrior() {
        int inclusionShape = GrainGrowthFront.getInstance().getTypeOfInclusionsComboBox().getSelectedIndex();
        int typeOfInclusionGeneration = GrainGrowthFront.getInstance().getTimeOfInclusionsInsertComboBox().getSelectedIndex();

        return inclusionShape == 1 && typeOfInclusionGeneration == 0;
    }

    public boolean isNoInclusions() {
        int inclusionShape = GrainGrowthFront.getInstance().getTypeOfInclusionsComboBox().getSelectedIndex();

        return inclusionShape == 0;
    }

    public void printAllStates(int[][] step0, HashMap<Integer, Color> colorMap, Graphics g) {
        int[][] step1;
        GrainGrowth grainGrowth = new GrainGrowth();
        ArrayList<int[][]> listOfMatrices = new ArrayList<>();
        int matricesCount = -1;
        do {
            step1 = grainGrowth.newStateMatrix(step0);
            printState(step1, colorMap, g);
            listOfMatrices.add(step1);
            step0 = step1;
            matricesCount++;
        } while (grainGrowth.containsZeros(step0).contains(0));

        grainGrowth.printState(listOfMatrices.get(matricesCount));

    }

    public void printState(int[][] stepMatrix, HashMap<Integer, Color> colorMap, Graphics g) {
        int p = 800 / stepMatrix.length;
        int q = 800 / stepMatrix[0].length;
        for (int i = 0; i < stepMatrix.length; i++) {
            for (int j = 0; j < stepMatrix[0].length; j++) {
                int grainLabel = stepMatrix[i][j];
                if (grainLabel != 0) {
                    g.setColor(colorMap.get(grainLabel));
                    g.fillRect(p * i, q * j, p, q);
                }
            }
        }
    }

    public HashMap distinctColoursGenerator(int numberOfGrains) {
        HashMap<Integer, Color> distinctColours = new HashMap<>();
        for (int i = 0; i <= numberOfGrains; i++) {
            Color newColor = colourGenerator();
            if (!distinctColours.containsValue(newColor)) {
                distinctColours.put(i, newColor);
            } else {//co w przypadku zduplikowania koloru?
            }
        }
        return distinctColours;
    }

    public Color colourGenerator() {
        Color newColor;
        Random random = new Random();
        int R = random.nextInt(255);
        int G = random.nextInt(255);
        int B = random.nextInt(255);
        newColor = new Color(R, G, B);

        return newColor;
    }
}
