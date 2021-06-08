package Spring.models;


public class RequestImages {
    private String path;

    public String getPath() {
        if(path==null) return null;
       /* var result = path.split("/");

        return String.join("\\",result);*/
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
