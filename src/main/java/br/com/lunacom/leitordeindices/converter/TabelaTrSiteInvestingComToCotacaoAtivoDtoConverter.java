package br.com.lunacom.leitordeindices.converter;

import br.com.lunacom.leitordeindices.domain.dto.CotacaoAtivoDto;
import br.com.lunacom.leitordeindices.util.DataUtil;
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
public class TabelaTrSiteInvestingComToCotacaoAtivoDtoConverter  implements Converter<WebElement, CotacaoAtivoDto> {

    @Override
    public CotacaoAtivoDto encode(WebElement tr) {
        final List<WebElement> tdList = tr.findElements(By.tagName("td"));
        NumberFormat nf = NumberFormat.getInstance(Locale.GERMANY);

        CotacaoAtivoDto dto = new CotacaoAtivoDto();
        try {
            final Date date;
            date = DataUtil.parseDayMonthYearDot(tdList.get(0).getText());
            Timestamp ts = new Timestamp(date.getTime());
            dto.setReferencia(ts);
            dto.setPreco(nf.parse(tdList.get(1).getText()).doubleValue());
            dto.setAbertura(nf.parse(tdList.get(2).getText()).doubleValue());
            dto.setMaxima(nf.parse(tdList.get(3).getText()).doubleValue());
            dto.setMinima(nf.parse(tdList.get(4).getText()).doubleValue());
            dto.setVolume(tdList.get(5).getText());
            dto.setOrigem("historico-ativo");
            dto.setImportacao(new Date());

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
