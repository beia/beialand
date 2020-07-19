package data;

public class ComplexLocation extends Location {
    private Integer idUser;
    private Long timeSeconds;

    public ComplexLocation(Integer idUser, double latitude, double longitude, Long timeSeconds) {
        super(latitude, longitude);
        this.idUser = idUser;
        this.timeSeconds = timeSeconds;
    }

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public Long getTimeSeconds() {
        return timeSeconds;
    }

    public void setTimeSeconds(Long timeSeconds) {
        this.timeSeconds = timeSeconds;
    }

    @Override
    public String toString() {
        return super.toString() + " ComplexLocation{" +
                "idUser=" + idUser +
                ", timeSeconds=" + timeSeconds +
                '}';
    }
}
