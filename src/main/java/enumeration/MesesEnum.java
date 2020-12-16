package enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;

@AllArgsConstructor
@Getter
public enum MesesEnum {
    JAN("01","Jan", "Janeiro", "",""),
    FEV("02","Fev","Fevereiro", "",""),
    MAR("03","Mar","Março", "",""),
    ABR("04","Abr","Abril", "",""),
    MAI("05","Mai","Maio", "",""),
    JUN("06","Jun","Junho", "",""),
    JUL("07","Jul","Julho", "",""),
    AGO("08","Ago","Agosto", "",""),
    SET("09","Set","Setembro", "",""),
    OUT("10","Out","Outubro", "",""),
    NOV("11","Nov","Novembro", "",""),
    DEZ("12","Dez","Dezembro", "","");

    String codigo;
    String SiglaPt;
    String DescricaoPt;
    String SiglaEn;
    String DescricaoEn;

    public static MesesEnum fromSiglaPt(String value) {
        return EnumSet.allOf(MesesEnum.class)
                .stream()
                .filter(it -> it.getSiglaPt().equals(value))
                .findFirst()
                .orElse(JAN);
    }
}
