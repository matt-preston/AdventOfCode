package utils;

import Jama.Matrix;

public class MatrixUtils {

    public static Matrix rotateLeft(final Matrix matrix) {
        return flipVertical(matrix.transpose());
    }

    public static Matrix rotateRight(final Matrix matrix) {
        return flipHorizontal(matrix.transpose());
    }

    public static Matrix flipVertical(final Matrix matrix) {
        final var result = matrix.copy();
        for (int srcRow = 0; srcRow < result.getRowDimension() / 2; srcRow++) {
            final var destRow = result.getRowDimension() - 1 - srcRow;
            for (int column = 0; column < result.getColumnDimension(); column++) {
                final var temp = result.get(srcRow, column);
                result.set(srcRow, column, result.get(destRow, column));
                result.set(destRow, column, temp);
            }
        }

        return result;
    }

    public static Matrix flipHorizontal(final Matrix matrix) {
        final var result = matrix.copy();

        for (int srcColumn = 0; srcColumn < result.getColumnDimension() / 2; srcColumn++) {
            final var destColumn = result.getColumnDimension() - 1 - srcColumn;
            for (int row = 0; row < result.getRowDimension(); row++) {
                final var temp = result.get(row, srcColumn);
                result.set(row, srcColumn, result.get(row, destColumn));
                result.set(row, destColumn, temp);
            }
        }

        return result;
    }
}
