import java.sql.Timestamp;

public class Game {

    private String teamOneName;

    private String teamTwoName;
    private Integer teamOnePoints;
    private Integer teamTwoPoints;
    private Status status;
    private Timestamp time;

    private enum Status {
        PRE,RUN,ONE,TWO
    }

    public Game(String teamOneName, String teamTwoName, Integer teamOnePoints, Integer teamTwoPoints, Status status, Timestamp startTime) {

        this.teamOneName = teamOneName;
        this.teamTwoName = teamTwoName;
        this.teamOnePoints = teamOnePoints;
        this.teamTwoPoints = teamTwoPoints;
        this.status = evaluateWinner(teamOnePoints,teamTwoPoints);
        this.time = startTime;
    }

    public Game(String teamOneName, String teamTwoName, Timestamp time) {

        this.teamOneName = teamOneName;
        this.teamTwoName = teamTwoName;
        this.status = Status.PRE;
        this.time = time;
    }

    public Game(String teamOneName, String teamTwoName) {

        this.teamOneName = teamOneName;
        this.teamTwoName = teamTwoName;
        this.status = Status.PRE;
    }

    public void setResult(Integer teamOnePoints,Integer teamTwoPoints){
        this.teamOnePoints = teamOnePoints;
        this.teamTwoPoints = teamTwoPoints;
        this.status = evaluateWinner(this.teamOnePoints,this.teamTwoPoints);
    }

    public String getWinner() {
        switch(this.status){
            case ONE: return this.teamOneName;
            case TWO: return this.teamTwoName;
            case PRE: return "Game not started yet";
            case RUN: return "Game still running";
            default: return "Something went wrong."; //TODO real error
        }
    }

    public String getTeamOneName() {

        return teamOneName;
    }

    public void setTeamOneName(String teamOneName) {
        this.teamOneName = teamOneName;
    }

    public String getTeamTwoName() {
        return teamTwoName;
    }

    public void setTeamTwoName(String teamTwoName) {
        this.teamTwoName = teamTwoName;
    }

    public Integer getTeamOnePoints() {
        return teamOnePoints;
    }

    public void setTeamOnePoints(Integer teamOnePoints) {
        this.teamOnePoints = teamOnePoints;
    }

    public Integer getTeamTwoPoints() {
        return teamTwoPoints;
    }

    public void setTeamTwoPoints(Integer teamTwoPoints) {
        this.teamTwoPoints = teamTwoPoints;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    private Status evaluateWinner(Integer teamOnePoints, Integer teamTwoPoints){
        if (teamOnePoints > teamTwoPoints) {
            return Status.ONE;
        }else{
            return Status.TWO;
        }
    }

}
