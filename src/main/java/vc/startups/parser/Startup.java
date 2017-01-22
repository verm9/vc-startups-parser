package vc.startups.parser;

/**
 * Created by nonu on 1/21/2017.
 */
public class Startup implements Comparable<Startup> {
    private String id;
    private String link;

    public void setUps(int ups) {
        this.ups = ups;
    }

    public void setDowns(int downs) {
        this.downs = downs < 1 ? 1 : downs;
    }

    private int ups;
    private int downs;

    public Startup(String id, String link, int ups, int downs) {
        this.id = id;
        this.link = link;
        this.ups = ups;
        this.setDowns(downs);
    }

    public Startup(String id, String link) {
        this.id = id;
        this.link = link;
    }

    public int compareTo(Startup o) {
        float v = (float) this.ups / this.downs - (float) o.ups / o.downs;
        return v == 0 ? 0 : v < 0 ? -1 : 1;
    }

    @Override
    public String toString() {
        return "Startup{" +
                "id='" + id + '\'' +
                ", link='" + link + '\'' +
                ", ups=" + ups +
                ", downs=" + downs +
                '}' + System.lineSeparator();
    }
}
