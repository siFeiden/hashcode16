package models;

interface Location {

    int getRow();

    int getCol();

    default int distance(Location other) {
        return (int) Math.ceil(Math.hypot(getRow() - other.getRow(), getCol() - other.getCol()));
    }
}
