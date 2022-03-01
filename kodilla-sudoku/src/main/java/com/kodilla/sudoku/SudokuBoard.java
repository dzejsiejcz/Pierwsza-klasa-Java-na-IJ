package com.kodilla.sudoku;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import static com.kodilla.sudoku.Statics.DIMENSION_OF_TABLE;
import static com.kodilla.sudoku.Statics.EMPTY_FIELD;
import static com.kodilla.sudoku.SudokuGame.backtrack;

public class SudokuBoard extends Prototype<SudokuBoard> {


    private List<SudokuRow> listOfRows = new ArrayList<>();

    public SudokuBoard() {
        for (int i = 0; i < DIMENSION_OF_TABLE; i++) {
            listOfRows.add(new SudokuRow());
        }
    }

    public SudokuBoard(boolean isCopy) {

    }

    public List<SudokuRow> getListOfRows() {
        return listOfRows;
    }

    public boolean fillBoard(@NotNull String filling) {
        // Creating array of string length
        String filling1 = filling.replace(" ", "");
        String filling2 = filling1.replace(",", "");
        char[] ch = new char[filling2.length()];
        // Copy character by character into array
        for (int i = 0; i < filling2.length(); i++) {
            ch[i] = filling2.charAt(i);
        }
        int coordinateX = Character.getNumericValue(ch[0]);
        int coordinateY = Character.getNumericValue(ch[1]);
        int value = Character.getNumericValue(ch[2]);
        System.out.println("Wybrałeś x= " + coordinateX + "y " + coordinateY + "wartość " + value);

        listOfRows.get(coordinateY-1).getListOfElements().get(coordinateX-1).setValue(value);

        return true;
    }

    public boolean solveSudoku() throws CloneNotSupportedException {
        boolean isChanged;
        boolean isSolved = false;

        do {
            isChanged = false;
            //checking every row
            for (int row = 0; row < DIMENSION_OF_TABLE; row++) {
                SudokuRow sudokuRow = listOfRows.get(row);
                for (int col = 0; col < DIMENSION_OF_TABLE; col++) {
                    SudokuElement currentlyField = sudokuRow.getListOfElements().get(col);
                    int currentlyFieldValue = currentlyField.getValue();
                    Set<Integer> possibleValues = currentlyField.getPossibleValues();
                    if (currentlyFieldValue == EMPTY_FIELD) {
                        for (int possibleValue : possibleValues) {
                            //jeśli ta cyfra jest wpisana w innym polu, usuwamy ją z tablicy możliwych cyfr,
                            // i jeśli została tylko jedna możliwa cyfra, wpisujemy ją do aktualnego pola,
                            if (sudokuRow.isInscribedInRow(possibleValue)) {
                                possibleValues.remove(possibleValue);
                                if (possibleValues.size() == 1) {
                                    currentlyField.setValue(possibleValues.stream().findAny().get());
                                    isChanged = true;
                                    break;
                                }
                            }
                            //nie występuje ani jako wpisana, ani jako możliwa cyfra w innym polu,
                            // wpisujemy ją do aktualnego pola,
                            if (!sudokuRow.isInscribedInRow(possibleValue) && !sudokuRow.isInPossibleValuesInRow(possibleValue)) {
                                currentlyField.setValue(possibleValue);
                                isChanged = true;
                                break;
                            }
                            //jeśli ta cyfra jest wpisana w innym polu, ale jest też jedyną możliwością
                            // w aktualnym polu, algorytm zwraca błąd
                            if (sudokuRow.isInscribedInRow(possibleValue) && possibleValues.size() == 1) {
                                return false;
                            }
                        }
                    }
                }
            }
            //checking every column
            for (int col = 0; col < DIMENSION_OF_TABLE; col++) {
                for (int row = 0; row < DIMENSION_OF_TABLE; row++) {
                    SudokuElement currentlyField = getElement(col, row);

                    int currentlyFieldValue = currentlyField.getValue();
                    Set<Integer> possibleValues = currentlyField.getPossibleValues();
                    if (currentlyFieldValue == EMPTY_FIELD) {
                        for (int possibleValue : possibleValues) {
                            if (isInscribedInColumn(possibleValue, col)) {
                                possibleValues.remove(possibleValue);
                                if (possibleValues.size() == 1) {
                                    currentlyField.setValue(possibleValues.stream().findAny().get());
                                    isChanged = true;
                                    break;
                                }
                            }
                            if (!isInPossibleValuesInColumn(possibleValue, col)) {
                                currentlyField.setValue(possibleValue);
                                isChanged = true;
                                break;
                            }
                            if (isInscribedInColumn(possibleValue, col) && possibleValues.size() == 1){
                                return false;
                            }
                        }
                    }
                }
            }

            //checking every section
            for (int row = 0; row < DIMENSION_OF_TABLE; row++) {
                for (int col = 0; col < DIMENSION_OF_TABLE; col++) {
                    SudokuElement currentlyField = listOfRows.get(row).getListOfElements().get(col);
                    int currentlyFieldValue = currentlyField.getValue();
                    Set<Integer> possibleValues = currentlyField.getPossibleValues();
                    if (currentlyFieldValue == EMPTY_FIELD) {
                        for (int possibleValue : possibleValues) {
                            if (isInscribedInSection(possibleValue, col, row)){
                                possibleValues.remove(possibleValue);
                                if (possibleValues.size() == 1) {
                                    currentlyField.setValue(possibleValues.stream().findAny().get());
                                    isChanged = true;
                                    break;
                                }
                            }
                            if (!isInPossibleValuesInSection(possibleValue, col, row)) {
                                currentlyField.setValue(possibleValue);
                                isChanged = true;
                                break;
                            }
                            if (isInscribedInSection(possibleValue, col, row) && possibleValues.size()==1) {
                                return false;
                            }
                        }
                    }
                }
            }
            if (!isChanged) {
                guessField();
            } else {
                isSolved = checkSolved();
            }
        }
        while (!isSolved) ;


        return true;
    }

    private void guessField() throws CloneNotSupportedException {
        Scanner sc = new Scanner(System.in);
        for (int row = 0; row < DIMENSION_OF_TABLE; row++) {
            for (int col = 0; col < DIMENSION_OF_TABLE; col++) {
                SudokuElement currentlyField = getElement(col, row);

                if (currentlyField.getValue() == EMPTY_FIELD) {
                    System.out.println("Aktualna tablica, podaj zgadywaną wartośc dla rzędu: " + row + " kolumny: " + col +"\n" +  this);
                    int guessValue = sc.nextInt();
                    SudokuBoard clonedSudokuBoard = this.deepCopy();
                    SudokuGuessingElement sudokuGuessingElement = new SudokuGuessingElement(clonedSudokuBoard, col, row, guessValue);
                    backtrack.add(sudokuGuessingElement);
                    currentlyField.setValue(guessValue);
                }
            }
        }

    }

    private boolean checkSolved() {
        for (int row = 0; row < DIMENSION_OF_TABLE; row++) {
            for (int col = 0; col < DIMENSION_OF_TABLE; col++) {
                SudokuElement currentlyField = listOfRows.get(row).getListOfElements().get(col);
                if (currentlyField.getValue() == EMPTY_FIELD) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isInscribedInColumn(final int requestedValue, final int requestedColumn) {
        for  (int row = 0; row < DIMENSION_OF_TABLE; row++) {
            int checkingValue = listOfRows.get(row).getListOfElements().get(requestedColumn).getValue();
            if (checkingValue == requestedValue) {
                return true;
            }
        }
        return false;
    }

    private boolean isInPossibleValuesInColumn (final int requestedValue, final int requestedColumn) {
        for  (int row = 0; row < DIMENSION_OF_TABLE; row++) {
            Set<Integer> possibleValues = listOfRows
                    .get(row).getListOfElements()
                    .get(requestedColumn)
                    .getPossibleValues();
            if(possibleValues.contains(requestedValue)){
                return true;
            }
        }

        return false;
    }

    private boolean isInscribedInSection(final int requestedValue, final int col, final int row) {
        int startColInSection = (col/3) * 3;
        int startRowInSection = (row/3) * 3;

        for (int sectionRow = startRowInSection; sectionRow < startRowInSection+3; sectionRow++ ) {
            for (int sectionCol = startColInSection; sectionCol < startColInSection+3; sectionCol++){
                if (requestedValue == listOfRows.get(sectionRow).getListOfElements().get(sectionCol).getValue()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isInPossibleValuesInSection(final int requestedValue, final int col, final int row) {
        int startColInSection = (col / 3) * 3;
        int startRowInSection = (row / 3) * 3;

        for (int sectionRow = startRowInSection; sectionRow < startRowInSection + 3; sectionRow++) {
            for (int sectionCol = startColInSection; sectionCol < startColInSection + 3; sectionCol++) {
                Set<Integer> possibleValues = listOfRows.get(sectionRow).getListOfElements().get(sectionCol).getPossibleValues();
                Integer possible = possibleValues.stream()
                        .filter(integer -> integer == requestedValue)
                        .findAny().orElse(null);
                if (possible != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public SudokuBoard deepCopy() throws CloneNotSupportedException {
        SudokuBoard clonedBoard = super.clone();
        clonedBoard.listOfRows = new ArrayList<>();
        for (SudokuRow sudokuRow : listOfRows) {
            SudokuRow clonedSudokuRow = new SudokuRow(true);
            for (SudokuElement sudokuElement : sudokuRow.getListOfElements()) {
                SudokuElement clonedSudokuElement = new SudokuElement(sudokuElement.getValue());
                for (Integer possibleValue : sudokuElement.getPossibleValues()) {
                    clonedSudokuElement.getPossibleValues().add(possibleValue);
                }
                clonedSudokuRow.getListOfElements().add(clonedSudokuElement);
            }
            clonedBoard.listOfRows.add(clonedSudokuRow);
        }
        return clonedBoard;
    }




    @Override
    public String toString() {
        String response = "                         SudokuBoard\n";
        String describCol = "      1     2     3      4     5     6      7     8     9\n";
        String dash = "   ---------------------------------------------------------\n";
        String upDash = "   _________________________________________________________\n";
        response = response + describCol;
        for (int y = 0; y< DIMENSION_OF_TABLE; y++) {
            String row = "";
            if (y==3 || y==6 ) {
                row = row + upDash;
            }
            row = row + dash + (y+1) + "  ";
            SudokuRow sudokuRow = listOfRows.get(y);
            for (int x = 0; x< DIMENSION_OF_TABLE; x++) {
                int value = sudokuRow.getListOfElements().get(x).getValue();
                if (value == EMPTY_FIELD){
                    if (x==3 || x==6){
                        row = row + "||  " + " " + "  ";
                    } else {
                        row = row + "|  " + " " + "  ";
                    }
                } else {
                    if (x==3 || x==6){
                        row = row + "||  " + value + "  ";
                    } else {
                        row = row + "|  " + value + "  ";
                    }
                }
            }
            row = row + "|\n";
            response = response + row;
        }
        response = response + dash;
        return response;
    }

    private SudokuElement getElement(int col, int row){
        return listOfRows
                .get(row)
                .getListOfElements()
                .get(col);
    }
}
