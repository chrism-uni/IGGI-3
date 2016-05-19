package utils;


public class Matrix {
    
    private double[][] matrix;
    private int num_rows;
    private int num_columns;

    public Matrix(int _num_rows, int _num_columns) {
        this.num_rows = _num_rows;
        this.num_columns = _num_columns;
        this.matrix = new double[this.num_rows][this.num_columns];
    }

    public Matrix(int _num_rows) {
        this.num_rows = _num_rows;
        this.num_columns = _num_rows;
        this.matrix = new double[this.num_rows][this.num_columns];
    }

    public int getNumRows() {
        return this.num_rows;
    }

    public int getNumColumns() {
        return this.num_columns;
    }

    public int MaxMin() {
        int row = 0;
        double max_min = -Double.POSITIVE_INFINITY;
        for(int i=0; i<this.num_rows; i++)
        {
            double min_in_row = matrix[i][0];
            for(int j=1; j<this.num_columns; j++)
                min_in_row = (min_in_row<matrix[i][j])?min_in_row:matrix[i][j];
            if(min_in_row > max_min)
            {
                max_min = min_in_row;
                row = i;
            }
        }
        return row;
    }
    
    public int MaxSum() {
        int row = 0;
        double max_sum = -Double.POSITIVE_INFINITY;
        for(int i=0; i<this.num_rows; i++)
        {
            double sum_in_row = matrix[i][0];
            for(int j=1; j<this.num_columns; j++)
                sum_in_row += matrix[i][j];
            if(sum_in_row > max_sum)
            {
                max_sum = sum_in_row;
                row = i;
            }
        }
        return row;
    }
    
    public int MinMax() {
        int row = 0;
        double min_max = Double.POSITIVE_INFINITY;
        for(int i=0; i<this.num_rows; i++)
        {
            double max_in_row = matrix[i][0];
            for(int j=1; j<this.num_columns; j++)
                max_in_row = (max_in_row>matrix[i][j])?max_in_row:matrix[i][j];
            if(max_in_row < min_max)
            {
                min_max = max_in_row;
                row = i;
            }
        }
        return row;
    }
    
    public int MinSum() {
        int row = 0;
        double min_sum = Double.POSITIVE_INFINITY;
        for(int i=0; i<this.num_rows; i++)
        {
            double sum_in_row = matrix[i][0];
            for(int j=1; j<this.num_columns; j++)
                sum_in_row += matrix[i][j];
            if(sum_in_row < min_sum)
            {
                min_sum = sum_in_row;
                row = i;
            }
        }
        return row;
    }

    public double at(int row, int column) {
        return matrix[row][column];
    }
    
    public void fill(int row, int column, double value) {
        matrix[row][column] = value;
    }

    public static Matrix subtract(Matrix m1, Matrix m2) {
        if(m1.getNumRows() == m2.getNumRows() && m1.getNumColumns() == m2.getNumColumns())
        {
            Matrix res = new Matrix(m1.getNumRows(),m1.getNumColumns());
            for(int i=0; i<m1.getNumRows(); i++)
                for(int j=0; j<m1.getNumColumns(); j++)
                    res.fill(i,j,m1.at(i,j)-m2.at(i,j));
            return res;
        } else throw new RuntimeException("The two matrix don't have the same size.");
    }
}
