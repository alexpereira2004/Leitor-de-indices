package br.com.lunacom.leitordeindices.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class DataUtil {
    private static String HOUR = "HH";

    private static String MINUTE = "mm";

    private static String SECOND = "ss";

    private static String DAY = "dd";

    private static String MONTH = "MM";

    private static String YEAR = "yyyy";

    private static String MONTH_YEAR = "MMyyyy";

    private static String MONTH_YEAR_SLASH = "MM/yyyy";

    private static String DAY_MONTH_YEAR = "ddMMyyyy";

    private static String YEAR_MONTH_DAY = "yyyyMMdd";

    private static String DAY_MONTH_YEAR_SLASH = "dd/MM/yyyy";

    private static String DAY_MONTH_YEAR_DOT = "dd.MM.yyyy";

    private static String DAY_MONTH_YEAR_SLASH_HOUR_MINUTE_SECOND_SEPARATOR = "dd/MM/yyyy HH:mm:ss";

    private static String HOUR_MINUTE_SECOND_SEPARATOR = "HH:mm:ss";

    private static String DAY_MONTH_YEAR_HOUR_MINUTE_SECOND = "ddMMyyyyHHmmss";

    private static String YEAR_MONTH_DAY_HOUR_MINUTE_SECOND = "yyyyMMddHHmmss";

    private static String YEAR_MONTH_DASH = "yyyy-MM";

    private static String YEAR_MONTH_DAY_DASH = "yyyy-MM-dd";

    private static String YEAR_MONTH_DAY_HOUR_MINUTE_SECOND_DASH = "yyyy-MM-dd HH:mm:ss";

    private static String YEAR_MONTH_DAY_T_HOUR_MINUTE_SECOND_DASH = "yyyy-MM-dd'T'HH:mm:ss";

    private static String FULL_TIMESTAMP_WITH_TZ = "yyyy-MM-dd'T'HH:mm:ssZ";

    public static String formatAsHour(Date date) {
        return new SimpleDateFormat(HOUR).format(date);
    }

    public static String formatAsMinute(Date date) {
        return new SimpleDateFormat(MINUTE).format(date);
    }

    public static String formatAsSecond(Date date) {
        return new SimpleDateFormat(SECOND).format(date);
    }

    public static String formatAsDay(Date date) {
        return new SimpleDateFormat(DAY).format(date);
    }

    public static String formatAsMonth(Date date) {
        return new SimpleDateFormat(MONTH).format(date);
    }

    public static String formatAsYear(Date date) {
        return new SimpleDateFormat(YEAR).format(date);
    }

    public static String formatAsMonthYear(Date date) {
        return new SimpleDateFormat(MONTH_YEAR).format(date);
    }

    public static String formatAsMonthYearSlash(Date date) {
        return new SimpleDateFormat(MONTH_YEAR_SLASH).format(date);
    }

    public static String formatAsDayMonthYear(Date date) {
        return new SimpleDateFormat(DAY_MONTH_YEAR).format(date);
    }

    public static Date parseDayMonthYear(String source) throws ParseException {
        return new SimpleDateFormat(DAY_MONTH_YEAR).parse(source);
    }

    public static String formatAsYearMonthDay(Date date) {
        return new SimpleDateFormat(YEAR_MONTH_DAY).format(date);
    }

    public static String formatAsDayMonthYearSlash(Date date) {
        return new SimpleDateFormat(DAY_MONTH_YEAR_SLASH).format(date);
    }

    public static Date parseDayMonthYearSlash(String source) throws ParseException {
        return new SimpleDateFormat(DAY_MONTH_YEAR_SLASH).parse(source);
    }

    public static Date parseDayMonthYearDot(String source) throws ParseException {
        return new SimpleDateFormat(DAY_MONTH_YEAR_DOT).parse(source);
    }

    public static String formatAsDayMonthYearSlashHourMinuteSecondSeparator(Date date) {
        return new SimpleDateFormat(DAY_MONTH_YEAR_SLASH_HOUR_MINUTE_SECOND_SEPARATOR).format(date);
    }

    public static Date parseDayMonthYearSlashHourMinuteSecondSeparator(String source) throws ParseException {
        return new SimpleDateFormat(DAY_MONTH_YEAR_SLASH_HOUR_MINUTE_SECOND_SEPARATOR).parse(source);
    }

    public static String formatAsHourMinuteSecondSeparator(Date date) {
        return new SimpleDateFormat(HOUR_MINUTE_SECOND_SEPARATOR).format(date);
    }

    public static String formatAsDayMonthYearHourMinuteSecond(Date date) {
        return new SimpleDateFormat(DAY_MONTH_YEAR_HOUR_MINUTE_SECOND).format(date);
    }

    public static String formatAsYearMonthDayHourMinuteSecond(Date date) {
        return new SimpleDateFormat(YEAR_MONTH_DAY_HOUR_MINUTE_SECOND).format(date);
    }

    public static String formatAsYearMonthDash(Date date) {
        return new SimpleDateFormat(YEAR_MONTH_DASH).format(date);
    }

    public static String formatAsYearMonthDayDash(Date date) {
        return new SimpleDateFormat(YEAR_MONTH_DAY_DASH).format(date);
    }

    public static Date parseYearMonthDash(String source) throws ParseException {
        return new SimpleDateFormat(YEAR_MONTH_DASH).parse(source);
    }

    public static Date parseYearMonthDayDash(String source) throws ParseException {
        return new SimpleDateFormat(YEAR_MONTH_DAY_DASH).parse(source);
    }

    public static String formatAsYearMonthDayHourMinuteSecondDash(Date date) {
        return new SimpleDateFormat(YEAR_MONTH_DAY_HOUR_MINUTE_SECOND_DASH).format(date);
    }

    public static Date parseYearMonthDayHourMinuteSecondDash(String source) throws ParseException {
        return new SimpleDateFormat(YEAR_MONTH_DAY_HOUR_MINUTE_SECOND_DASH).parse(source);
    }

    public static String formatAsYearMonthDayTHourMinuteSecondDash(Date date) {
        return new SimpleDateFormat(YEAR_MONTH_DAY_T_HOUR_MINUTE_SECOND_DASH).format(date);
    }

    public static Date parseYearMonthDayTHourMinuteSecondDash(String source) throws ParseException {
        return new SimpleDateFormat(YEAR_MONTH_DAY_T_HOUR_MINUTE_SECOND_DASH).parse(source);
    }

    public static String formatAsFullTimestampWithTz(Date date) throws ParseException {
        return new SimpleDateFormat(FULL_TIMESTAMP_WITH_TZ).format(date);
    }

    public static Date parseFullTimestampWithTz(String source) throws ParseException {
        return new SimpleDateFormat(FULL_TIMESTAMP_WITH_TZ).parse(source);
    }

    public static LocalDate parseToLocalDate(Date date) {
        return new Date(date.getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static boolean dueDateIsNotExpired(Date dataPagamento, Date dataVencimento) {
        return !parseToLocalDate(dataPagamento).isAfter(parseToLocalDate(dataVencimento));
    }

    public static Date setEndTime(Date date, int hour, int minute, int second, int millisecond) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, millisecond);
        return cal.getTime();
    }

    public static boolean dueDateIsExpired(LocalDate dataPagamento, LocalDate dataVencimentoLocal) {
        return dataPagamento.isAfter(dataVencimentoLocal);
    }

    public static Date getStartOfMonth(Date data) {
        LocalDate day =  parseToLocalDate(data).withDayOfMonth(1);
        return Date.from(day.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDate convertDateToLocalDate(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public static boolean isWithinRange(Date testDate, Date startDate, Date endDate) {
        return testDate.after(startDate) && testDate.before(endDate);
    }

    public static Date getCurrentDate() {
        return new Date();
    }

}
