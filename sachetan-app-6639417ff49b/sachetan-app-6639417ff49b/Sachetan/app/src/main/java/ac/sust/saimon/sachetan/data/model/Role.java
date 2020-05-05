package ac.sust.saimon.sachetan.data.model;

public class Role {
    private static final long serialVersionUID = 7169843323823440283L;

    public static final String AUTHORITY_ADMIN = "ADMIN";
    public static final String AUTHORITY_USER = "USER";


    private String id;
    private String name;

    protected Role() {
    }

    public Role(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString(){
        return "[" + this.id + ", " + this.name + "]";
    }

}
