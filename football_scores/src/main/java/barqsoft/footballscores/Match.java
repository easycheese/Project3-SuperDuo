package barqsoft.footballscores;

import android.database.Cursor;

/**
 * Created by Laptop on 8/23/2015
 */
public class Match {
    private String nameHome;
    private String nameAway;
    private String time;
    private int goalsHome;
    private int goalsAway;
    private int day;
    private int league;
    private Double id;

    public Match(Cursor c) {
        nameHome = c.getString(c.getColumnIndex(DatabaseContract.scores_table.HOME_COL));
        nameAway = c.getString(c.getColumnIndex(DatabaseContract.scores_table.AWAY_COL));
        time = c.getString(c.getColumnIndex(DatabaseContract.scores_table.TIME_COL));
        goalsHome = c.getInt(c.getColumnIndex(DatabaseContract.scores_table.HOME_GOALS_COL));
        goalsAway = c.getInt(c.getColumnIndex(DatabaseContract.scores_table.AWAY_GOALS_COL));
        league = c.getInt(c.getColumnIndex(DatabaseContract.scores_table.LEAGUE_COL));
        day = c.getInt(c.getColumnIndex(DatabaseContract.scores_table.MATCH_DAY));
        id = c.getDouble(c.getColumnIndex(DatabaseContract.scores_table.MATCH_ID));
    }

    public String getNameHome() {
        return nameHome;
    }

    public String getNameAway() {
        return nameAway;
    }

    public int getGoalsHome() {
        return goalsHome;
    }

    public int getGoalsAway() {
        return goalsAway;
    }

    public Double getId() {
        return id;
    }

    public String getTime() {
        return time;
    }

    public int getDay() {
        return day;
    }

    public int getLeague() {
        return league;
    }
}
