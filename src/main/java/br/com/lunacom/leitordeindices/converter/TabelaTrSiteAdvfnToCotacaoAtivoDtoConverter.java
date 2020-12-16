package br.com.lunacom.leitordeindices.converter;

import br.com.lunacom.leitordeindices.domain.dto.CotacaoAtivoDto;
import br.com.lunacom.leitordeindices.util.DataUtil;
import enumeration.MesesEnum;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Component
public class TabelaTrSiteAdvfnToCotacaoAtivoDtoConverter implements Converter<WebElement, CotacaoAtivoDto> {

    @Override
    public CotacaoAtivoDto encode(WebElement tr) {
        final List<WebElement> tdList = tr.findElements(By.tagName("td"));
        NumberFormat nf = NumberFormat.getInstance(Locale.GERMANY);
//        Timestamp ts = new Timestamp(date.getTime());

        CotacaoAtivoDto dto = new CotacaoAtivoDto();
        try {
            final String[] s = tdList.get(0).getText().split(" ");
            final String codigoMes = MesesEnum.fromSiglaPt(s[1]).getCodigo();
            final Date dataReferencia = DataUtil.parseDayMonthYear(s[0] + codigoMes + s[2]);

            dto.setPreco(nf.parse(tdList.get(1).getText()).doubleValue());
            dto.setAbertura(nf.parse(tdList.get(4).getText()).doubleValue());
            dto.setVariacao(nf.parse(tdList.get(2).getText()).doubleValue());
            dto.setMaxima(nf.parse(tdList.get(5).getText()).doubleValue());
            dto.setMinima(nf.parse(tdList.get(6).getText()).doubleValue());
            dto.setImportacao(new Date());
            dto.setReferencia(dataReferencia);
            dto.setVolume(tdList.get(7).getText().replaceAll("\\.", ""));
            dto.setOrigem("advfn");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dto;
    }

    @Override
    public WebElement decode(CotacaoAtivoDto input) {
        return null;
    }
}
