package models;

/**
 * Stores an information about index level of particular parameter
 */
public class IndexLevel {
    private int id;
    private String indexLevelName;

    @Override
    public String toString() {
        return String.format("%-25s%d\n%-25s%s","     id: ", id ,
                "     indexLevelName: " , indexLevelName);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIndexLevelName() {
        return indexLevelName;
    }

    public void setIndexLevelName(String indexLevelName) {
        this.indexLevelName = indexLevelName;
    }
}
