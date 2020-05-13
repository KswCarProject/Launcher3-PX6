package org.apache.http.message;

public class ParserCursor {
    private final int lowerBound;
    private int pos;
    private final int upperBound;

    public ParserCursor(int lowerBound2, int upperBound2) {
        if (lowerBound2 < 0) {
            throw new IndexOutOfBoundsException("Lower bound cannot be negative");
        } else if (lowerBound2 > upperBound2) {
            throw new IndexOutOfBoundsException("Lower bound cannot be greater then upper bound");
        } else {
            this.lowerBound = lowerBound2;
            this.upperBound = upperBound2;
            this.pos = lowerBound2;
        }
    }

    public int getLowerBound() {
        return this.lowerBound;
    }

    public int getUpperBound() {
        return this.upperBound;
    }

    public int getPos() {
        return this.pos;
    }

    public void updatePos(int pos2) {
        if (pos2 < this.lowerBound) {
            throw new IndexOutOfBoundsException("pos: " + pos2 + " < lowerBound: " + this.lowerBound);
        } else if (pos2 > this.upperBound) {
            throw new IndexOutOfBoundsException("pos: " + pos2 + " > upperBound: " + this.upperBound);
        } else {
            this.pos = pos2;
        }
    }

    public boolean atEnd() {
        return this.pos >= this.upperBound;
    }

    public String toString() {
        return '[' + Integer.toString(this.lowerBound) + '>' + Integer.toString(this.pos) + '>' + Integer.toString(this.upperBound) + ']';
    }
}
