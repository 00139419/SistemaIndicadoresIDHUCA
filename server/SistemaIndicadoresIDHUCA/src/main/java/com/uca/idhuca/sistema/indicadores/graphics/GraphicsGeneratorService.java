package com.uca.idhuca.sistema.indicadores.graphics;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.*;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;

import com.uca.idhuca.sistema.indicadores.graphics.dto.GraphicsRequest;
import com.uca.idhuca.sistema.indicadores.graphics.dto.GraphicsRequest.ChartType;
import com.uca.idhuca.sistema.indicadores.graphics.dto.GraphicsResponseDTO;
import com.uca.idhuca.sistema.indicadores.graphics.dto.SeriesDTO;
import com.uca.idhuca.sistema.indicadores.graphics.dto.StyleDTO;

import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.ChartFactory;
import org.jfree.util.Rotation;


import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GraphicsGeneratorService {

    /* ===========================================================
     *  API PÚBLICA
     * ========================================================= */
	public GraphicsResponseDTO generate(GraphicsRequest req) {
	    JFreeChart chart;
	    
	    boolean allZero = req.getSeries().stream()
	            .flatMap(serie -> serie.getData().values().stream())
	            .allMatch(value -> value.doubleValue() == 0.0);

	        if (allZero) {
	            // Si todos los valores son 0, devolver una imagen por defecto en base64
	            String base64 = "iVBORw0KGgoAAAANSUhEUgAAAS0AAAErCAYAAACLutXzAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAE3CSURBVHhe7Z11zCVHdrevaTxjnBnvrr1eCigMCoOC0hdGReEoTAopiRIFpLAiBZRIkQJKokRhUOiPMDMz44LX9tprez2GGfMavn7q7dNTXbeq+hR013t36pHqfRvqV+dUdfXp6rp9+1723MCu0+l0DoTLx/+dTqdzEPSg1el0DooetDqdzkHRg1an0zkoetDqdDoHRQ9anU7noOhBq9PpHBRvUM9p9UfOOp0jLjN/zN83OI510LJdY1nSuGHH0nPPPrt76qmndk+9/vVm39NPP222PTsmuwz3IJr9Q1qVSMcp6VKXpXTIQN4S+yoWfFzNvrJtSu3v6VOOiY8F/eWXXz5k2c8jW9hHuuKKK46Wh/xXDOnKq64y2y5Ha+mlLPf/cedYBi1ceuLxx3ePPvbY7sknntg9MaTXD0GJgER6ZkgSkFTuOwcj+dCU6D0doVQPqjJW0ArRvQtaWEWv0AmletjLXWI/0TZMCqXWBLIhEbwIYldeeeXuiiGgXTX8J7CdGNLJU6d2J06cGBXHk2MRtHCBYESAuu+++3YPPvjg7ukhSBnkgOS46RzM5G5RWQ/qMjxaUOkDWljUR7QQ3bughVI9eHOVaGED28Isd6IWJkWGFozK0rojLILZDTfeuDtz+vTuCgLbEOCOE02DFqOlhx96aPfAAw/sLly4sHvqyScvjp7sRh3/J1Gi93QGdRmVtVCqh8UyIlqI7l3Qwmp6hRa8uZRaYZa7RAuJepgUGVowKm17DflIV1999e7aa67Z3TAEMJbdANeCJkGLYMUt3+2vfvXuwvnzZn2G1TDJTVRJKySVUaIPdAaVPqCFRX1EC6V6COZQaMGbS6mF6voELcxyJ2phUmRooYae+bHTZ87sbrrpJjMSaxm8Ng9aBKu777pr98C5c+aWcMbYEFnN4TRiUhmeA6DWV9YKqjJK9BEtrKpf0EIwh0Ir7OVM0EJVfaIWqukztGBUjvaqq64ywevMkFrdNm4WtORW8Lbbbtu9/qmn5hPoVsMkN6/TqEl6z8Es1YOqjBItrGBbiO5d0EIwh0IL3lxKLbTWwyz3hlqYFBlaITaSktvGF956a5Nbxk2C1jPPPLN7zZ137u67916zPGFVNrnaTkNtqvccJLU+cIBV+oBWiO4t0cKKtoVgrhK9Ugt7ORO0MMudqIVq+gwtGFWCltvEs2fP7s4Ot4xbBq7Vgxa3gPfec4+5JZwCllXBrKqW6D2Nqy4jcGBU+hItBPSwWEZEC9G9C1pYTa/QgjeXUivs5U7Ql2hhljtRKxhVphZygw46Pmm8+eabN7tdXDVo8WzVvcPo6rUSsKyGSW4ip1E31XsOqFrv0QqLZUS0UKIvtQ3RHCV6hRa8uZRa2MuZoIVZ7kStMKlK9I1sA0GLdM0115jbRUZfa7Na0OIp9XP337+7++67TfASsprHadSkMjwHRK0PHMxN9AEtlOohundFLQRzKLTCXs4ELVTVJ2qhmj5DC0aVqQXfyOzkyZO7F734xWayfk1WCVoEKZ69ImDxwKiQ1EROoyQ3b2U9qMsIdAaVPqCFRX1EC9G9C1oo1YM31wZaKNXDLHeiFiZFiRYy9GBUmVqI3kYO+06dOrW7dRhxrRm4qr/lgTksnmjnyXYJWFQzqZmchklu4hI9Wo9eVYZHCyp9QCuo9BGiexXaYI4FvwVvGUoteHMptVBiG8g5y52ghZk+UTsj0W/B2M/UgtwGRhnGP3z97rXDYGXvcaaKVB1p8VjDQw89tLvnnnvMQ6PJX72xGiW5aT0NmlRGiT5wMFX6SEdY1Ee0EN27oBWCuUr0G9mGvZwJWpjlTtTCpMjQQhV9plZYDFYO5Gdy/pZbbknWaqg20iL2PfLII2Ye65HGAYs1dRloc/UerbCoj2hBpY8Q3bugBXIEc5XoFVoIahP0s5wJWvDqE5kUGVqoos/Ugmp05YFYcP7hh3cPDXdcK8w+1Qtajw/DQp5y5wFStaM0iCRWx6TC0UKW3kKt92hB9ItleLSg0gdsC1H9ghaCetHm6JVaiOoV7OkTtFBLbxSJWpj0ok3UwxRsMrSQG6zA1jKvzQdytakStHic4cHBwdfdd9/84dEYTqMkNZFHq9ajdfSQpPeg0gdsw6I+ogX2BPcuaGFRv0BQr9CCV6/wWwjqE5jlTrAtTLkztDDTZ4AqN9hASbAyPjta5rX2HiivQJWgxTwWzu198dlFKjZWjr+SVDgNU6IVVGWI1tGLdr7VQ8A2LOojWojqF7QQ1Iu2VL+AV6/UCiV6ckkyZNie9IlawehFm6MfNJM+g6JgNWD0/D9ancEdGPGhJsVB69FHH93deccdy8NAp1GSm6hE7zkgbFGVETiYam1EHy0jogWVfoFgDoUWVtErtUDOWW60ifqJRC1MuTO0gMKoMrRg9JlaoUg/aJf0Mr9lP6tZSlHQYtj3GgLWk0+OWzxQqbFi/JWkQrRWw6j1Hi2o9AEtJOk9LOojWqil9+ZY0EJQL9oFPezpE7QQ1CuZ6RO1MOkztFBFn6kFMzIaUzKWVqsmYPEYVC2yg5b5tPDChd3DQxT1TrzTIFajJDWPowXW1GU4WlDrPVphUe/xW2CrSh8hujdiWwjuVWghqldArr2cSi3s6ZV+CzN9ohb29IlM+gzbwqTPJCtQCYM2R0184Bax1mirKGjdcfvt+wHLOSAsqSvqaEGtF22p3kH00TICWljUQkQP0TKUWm+OBa3g1Ys2R5+ghaBeyUyfqIVq+gwtGP2gMwEnRz9qswOW6MfVJLBJGgOXd4CTSHbQ4vXITLJNiHMjLF1cU2BpIUnvaIVN9AEtLOrRLuiDexe0UKIFcnhzKbQQ1Cvx6pW2hVnuDO2kULaZi1FkamHSZ5IdqGDQZgcrGLWif/yxx5Y/rFOQHbS4NZyiptUwtpMq0ObqHS2IfrEM0eboA1pBrQ8Q1S9oYVG/QFCvsA1evWhz9AlaEP2UO0ELJVrBlFGozdXDpM9h1GapsRnQP/nUU1Uef8gKWkRLnn4XB4G/rpNRLK2g1nu0kKT3oNIHtMCeaBkBvwWVPkJUv2AbgnqFFqJ6JXs5E7Qwy63022bKnaEFFEXBxvzJ00JRsBrIDlYw2g3p+Y1RnjbgVrGE7KD12DDUE5IqScWsRmVJ0iKOFtR60Tp6SNJ7WNRHtBDVi7ZUHyGoV2jBqxdtgn7KmaAF0U65E7TCpM/QwkyfgdFn2oaiYDVqs/RoRM/q0dYgfCe5dFYrK2g9+eST5ncJNU5OjJWzKdFCkt4DWxfLCNgGtT5CdO+CFoI5In4L7PXmUGiFoF7JXs4ELdTQT4pELUx6tBl6MMGiQJsVbAT042Iyo90UPbGj9BYxK2jxRUg1VMxqVJYkqfAcELXesS2o9AEtLOpFu6D37l3Qgmi9ORa0QqlW0owSvVIrzPSizdFnaGFPn4jRD7rcgFMlWJHG1SQsbbJ+0BG4SsgKWucZ4mnuSwcHbZIqiNajV5Xh0YJKH9AKKn2E6N4FLQRzLPgtkMObS6GFoDZXn6AFcs5yJ2hhpk/UCkaV6LeNCTaZWmgWrKBQy3xW6Zeos4LWY0ymhcAxSSMsXVxbwNGCWu/RCov6Ba0kL6It1UcI6hVaWEWv1MKePkELM71oc/QZWhB9bsCZ6TMxwSZXP2qz1NjM1Y9aox838ROCJSQHLZ5qDT5rgXMO6kpK5SxYU+k9WkFVRkALJVqooQ/mWNBCUI9WoYe9XAlamOXM0O7pE0lXzJn0GbbBqBLrbdMsWMFoN0tvaW09c1olD5kmBy3v0A7nRgdBnLQdDeJoBZUWPFpg62IZEduL+oBWiOpFm6NXaGFRv4DoZzmVWgjqE9jTJujJKWnS5ugztMJMn0GzYIVNS59chuhZPNoy45lh0LNd0BoMzb4/NDonhJwMYmkFdRmObUGlD2ihRAvsiZYR0UJUv6AVvLkW/LYJ6hWQay9ngm2YlZGohRItzPQZoGoZrEiZ6snnLL1lO6Z/dsuRFmZM0Bqds4k5OUO0Hv1iGaPusssvH/4dHVhJl4/J3S7J1vtgq3/PiEdrlx+1j79jChG1P5RhUgBsiP09H8S2u31INmJ/zwr5nLwhSrRAzil3ohYmfYYWjF60pfoMfMclBaPn/9FqGqPtLD0+j35rtEwvlQStpB+2ICsv+7vjjjsmo0kVHCtmo9YPWn4I8tprr91ddeKEOTndhjL/PTZ4gyJP4j5h/ZyZsJ/bwefzsO2GG24wP1Apv6q719kc31g3fjzyiPn01Sbqg8e+gM3rr79+d/3gywl+smlYn+X2+ADMKfBwMK8LeSb0zfuIXZe9nAlamOVO1AqTKkNfooVSPez1nxQGbbZ6tJult3xO0XMev+SlL83+YdfkOa1nrWClcpSKSbJI1csJevXVV18cTbD7KNfRf/JamDxD4jfY+HWQE0OwE2xtEKc8oLzTp0/vrrvuuqOANdqYwTb+Ha1N5Vw1HKQbBz8IMjDLY0N+SQHYc+rkyd2Zs2dNvaQ9XO3R3wGrLPymLZ93001GN8PSLkGuWc4ELcz0iVoQvVEV6HO0gmm/TL05ZmPKYdKP60lgc9Qm60etWRxTCkSQkpFWetAahnZqJ8eKueToCTyc9OAeZLPmbrPXh+XLh1skAh5b5zk9oHXKE64cTviTlDPm2ctlb7PKkW3cqt04BC0zUnSx8seQHAQ/6jUpHG1w+7BOOjWMFNGb/ZIUkGuWM0Er7OkTmRSZto0iQyuYMjK1UKLF52w9OvQsHm1JY7SbpR9tlwQsSA5afOlxkdE5G6nkfKsH0Tp6goWcbDZmzd5m5+E/6ywOiTKijPlDsMcEifFEn+W0bBmscuxtLFOG3FYaRu0S5LBzXTkEcrPu6Kd8vu3WuvEjYYhu9EeLRzjlLyH6SVGiT9QKJVqY+mCO7VFrH4MkRD+uJjNqs/T4nKsftWaRP1sHLWb+g1jO2exvCeDRgtnqKXvaLv+HNM8x5lnCU7YNe2Z7bTuWdrZtxN52ceuIpY1BDp/WbHP005q1nSVvXspwtoXYy6XUCbPcaHP1GVpAYeqaoYVJ34jiYJWrp85jvZP1lhZsffBZTwXpQcsXJR3ngDVJUUTr6MHWu3unDjgm9s/yjNvM4phMXhvRB5h0DtM2S7u07eLWkYhdgRzeXKJ1ypjWxu38dbeB2a6wD3tlSFIg2il3glaY9BlamOkzMPpM22CCxZiSKdECOvTjahKWVpKaUWsWrWQTHfwskB607AhpOSf4HPTi0QpLZUwHcfzv5jUHWpbNn+GvrbHXPbAnuNcuh39jMgS2zfZLisBeb46Idto67p/lsjRmSWl/yhWx62OmhUQ9TGUUaHP1YPSZWigNNnYfTgKblj6pjFFr9KwebdUj+pGY3jv4UZI+pyXGLOcEdSU9WmBrrAyzX7Tjfzv/bP+AWQrY8mH0R4t+QrZZHxLr0zYYtxlEG2FPL4zlh5j2jHlmOS2dWVrwY2/vQn6XWW60GfpJkaiFmTZTb/pQhhZMsMjUGpvox9VkRrtZektbogdNGZt+emiMWQ6CxkkDOkcLKr2tHf+Lhv9uRzNLdn47ObBlf+tIQDetjdvne9k8dj5Hy9Je3jHt4WhdJp2Vb5bbseuWZa+xPNtrlalhpk/UwqQXbY4+UwszfQbmeGdqQfpLVgmj7Sw9Po9+Z2stvbaMzea0CFiSQO2kVTEbld7VWg00/XfKnm1z9tmwJ7x3IKCdtjq+CFPndfRmjW3W9nkOC0cbZMzH30nhs+GUJ2v8n+1xtEvM9Ila2NMnMukztGD0GX7blAQr7GbrR7+zrY92+ZtchuVzsn7QbjoRD2onx0Z1UekDWhC92evkmW2z9rHkpiAR2xPD/r1y2CY6R2/WrG0szXMMsF/SAibHmG/K7WhZMmu+bWDbcbRL7JWToIVJL9pSfSJGn6kFtJJymPTjehLYHLXJ+lGbpbe0ZnVMaixtCVlBa5GIc4suxyo2bJ/tcfKZNdlm7ZuWQuUK7F/IY/YOefZy2ducMkQjy/O9Iwt2BVc/LftsQqhc2c5/pW0g55Q7USvM9ImgMKpM2zDpM8kNVIZBm61Hh57Foy1pjHaz9JbPpfpS0oNWbAJtbFQXqeSi24GK2XpJbl7TEew0IkvRq5qj8cHeqazxv2HUTtuccszauG2+Z2DUuhof5JjlGjTTuqM3a065LE1r4/aLe5eZ6cGxuYTojcrxTUOJVjB9oIY+h1GbpcbmqN1cP2rN4pjUiNbRH63lU2+kNTrmonLQqpiLd6udd1ieOpJThqzJfjMXZwddtjsaF/YGc4zaab/PvptHWLArkCuU09QkYhNmerY7/sj8ZIxJD1YZWqbcGVowikwtoMoONgMm2OTqR2229VGbpcfn0e9kvaWFLL3Fnl7R70KUBS2pmMdBSVE8WsHVT+/xkvzD/1lncMqRNbuzPfP000cLbHPyu7DXl4MJRPOMyWjb5HHKm22XZQtegrb0iyRTGTZiZyyXFzLaQcdsdfyYcLcP6zE/yCPJYNnVMNMnagWjz9SC6R819DmM2kx1mR6fR60kNaPWLFpJhWhHPSTplSQHrenqbjlmo3JwQe/bw2tdnnziicl+yI7o3Q7HyWleTROwaxPMMWifHsrhNTeLI5TRzqysYRsB78EHHgjqye+17/GbV9xI0DF7rTyz3O72YR37/Eq471OcPa3Hdowpd4YWUBhVhhaMPlMrlOizgw1gN1c/as2i+ZuApYUsvQVrsTIWzp4oSe/TooO/4hWv2J1/+OFxy0VUlXQqZrOoH7R8yZh3WF198uTsC8e21u1sVI8RCb+IPXvrqkPUvntAhnXe62Xe7RV4j9W0PmrxA/v8WKX9Q7c2ez44dn3wpgZed2PePMEXuQdc2zZsef1wAbgwBCx8mQU9G4Vtl5miRJ+hBaPK1EJRoBu0BerJ76wySrRg1Tu5DKfNNHramVcqnTlzZtySRnrQevnLZy+xU1fSqZyg0udoAxofwZyKMqJXVUcfamqvPsV/1weF1vZlL3eCbUi17TIpMrRCUcAZaBawRrtN9Fadk/VOe6XoS4NW9pwWTi46SsUkOSzqI1qIahWgl7RHxK6NyTGc/ASAKbFJkrPPxWtfaRsmvdhg0azO7foSTHoQu0rb4NUnMOkztGD0gy434Ig2Vz8Ij/TjahLYHLWb60etWRyTGksLSXrRWvocsoKWymTEsUX9QqWiexWNEtyr0AI5vLkUWvDqlbYFr14JOWe5E7Qw0yf6LRhFphZMoMnUQnagGskOVjBqs/RWvZP1jjZJb2khS28hF84c0oNWzJhUzHFQYKt/z4BS6987ENEKi/oFgnqFbfDqlVoQ/ZRbtIl6Q6JW2NMnQG6TMrQw02digk2uftRm6dGIftyUhOhZHJOaUWsWx6TG0golellKKsMh+/ZwD6diNuwpcXJRG7ENUftOo/oI6hVaYS9Xgha8+gRmuRO1gGJSZeoNGVowKrS5+kGXHawGsoMNjHaz9Ghz9Y42S2+RVIZlW7DX8sdZOUHLccTnnMBW/54R0eboF7Sg0kcI6hVaEP0sp1ILpXqY6RO1MOlFm6PP1MJMn0FRsBq1WXo0omf1aKse0bM4JjWjVkjSgkevLsPRgqtXlxUg7/ZQHHOcE9i66FhAK0T3KrTBHBG/bbw5lFoI6pXs5UywDeSccidqYU+fgVEVaE2wyNUPuqxgI6AfF5MZ7TbRW3VmKakMtLl6Ryt49UMMuWzTOS2PYwJ7wnsHpGKBMkTv3bugFYJ7FVogx16uRG2uHvb0CVqY6RO1MOlFm6nPDTglWqEkWEmwyyrB0ibr8XlMyXpLa1bHpMbSQpLe0YLoQ2U85+RPpdqc1qIbC46WVSPeSEu2IahXNnBQm6Cf5UzQglefyKTI0IJRJfotoJj0mUjAyaJQa/TjajKjXf4ml2H5nKwf/bZJ1juo9R6tlqKghVlJQTwNYxPVizZHr9CCV6/UQlSvYE+foIWZXrQZeqNI1MKkz9AKk7ZAnxtwRJulxuaoTdaP2mI9i2NSY2lB9KoyRFuit/9nkBW0VA46FXOJlrGghUX9AkG9QgtevcJvYU+foBX29IlMigzbMNNngCo32EBJsDI+F2pRZ5Uw2i3VQ7LeqXOS3tGCWo/Wo8+h2u3hxIJz7AnuVVQsqBdtjl6phaheQVCvRPSTIsE2zPSJWsHoRZujHzSTPoPSYGX0LB5tSaNQaxKL5m8CorXsq8uwtGbVSiosraDWB7Ql1AtaHuds2BN1NqKFqH5BK+hyhfHqlbZhL+dCm7nMciZqYcqdoQUURpWhBaPP1EJRsAL042Iyo+0sPT6PfvM3uYxRCyVaSNJbfgusqcrwaEGlXaA8aAWcE9gTdbREv6AF0e/lEm2OXqkVgnoFop1yJ2iFSZ+hhSr6TC2UBivRZ5WA3UKtWRyTGtGOtpP0llbI0luo9R4tqPUKyoKWxzlh0clA5WyCexVaiOoVeHMptUDOWW6l30KJFshtFBlaKNXDpM+kJFhhN1uPDj2LR1v0jFqzOKYkLJ9LtJBsv0TvaIUk+wrSgxaOSQoQdVKhlbTHghaCetEu6GFPn6CFoF7JTJ+ohT19IpM+wzYY/aAzASNHP2pLAo7Rj6tJYHPUZuv5N6YkRttmcUxqLC0k6UWbo/doQfTzrXWoOhEfddJTMZcSLUT1Csi1l1OphVI9zHInamFSoM3VZ2ph0meSHahg0GYHKxi1WXr8ztWPWiFJ79Em6x3Ueo8WFvUBnZYqQQsXgm44jeoiWm+OBa2wil6phT29aBP1JneiFkRrFIlawegLtbl6mPQ5jNosNTZr6Fk82qJn1JpFK6mwtJCltfSgKmNBO9/qwdHlUBS0ok56KuYS3auoHDm8uRS2watXagWvPoFZ7kQtTIpEvwUURcHG/MnTQlGwGsgONjBqs/T4PPqdrLe0kKR3tJCsd2BLUhkOaq3Hdg7JQUsqGDTvaVSX1fQKLXj1Si2Ifsot2lJ9ApM+QwszfQZGn2kbioLVqM3SoxH9uCkJ0bM4JjWj1ixaSc2oFZL0lm1BrRdtjj6gLaHqnNaSY+wN5lBULKhPaJSgXslezgQt1NBPikQtTHq0GXowwaJAmxVsRrKDDYx2s/RoS/UjyXrLNrCkLsPRQrLeQa33aEFtO0CdoOVpGBv2BPcuaAVvDqUWyLWXM8H2nj7BNsz0os3RZ2hhT5+I0Q+63IBTFKzQFeqNfRaPtugR2yyOKYlRbxbHpMbSQpLe0YJaL9ocfUALKr2C5F/jeeUrXmF+L8/gcczFl+Pyyy/fnTx1apAXVCHTdgy1P4l+q3J7ykz1P0RRO0Oh3qtWlplt2Sp/r4yM+iQrPDbUZTjaGrZLkRKJAfwkn5eIXXfPjadPb/cTYiZoPfLIuCVM0P2hYidOnNi94AUv2F1u/XYhFJ9cmbSyC73O23GodU5SrlxHfrtz78eGIzZDe7YNWq985cWRlodok42VM0Hr5punH1ztnXhbentvxxuaXYLWA+fOHQWtiI2o9UHHDwxv/ruHLjjpdZSKSbKgUSVtSSu70Mq2bbel7S2x7ba0vSWb2aX8gA22Rq1X8K04aEWdXLvxEmjRiYRWtnudt+VSrLOA9UUPKvmYHbSCTuKYpMbIwdz6gNp2W9reEttuS9tb08p2K7suWF/0AB8r+pkctKJOKhyL6ivR8mC2tNvCduu2blnnlra3xmd30Qvye3wt9X6T57SAvaXOxpBG3fqA2na3tN3KLrSyC61st7ILrWyH7LIW9YT8AV9la1S/QFnQijgnsLfEwRihRl2bS80utLJt221pe2ta2S6yiyaiq1WTvKC14JzgzaHUhihq1EIuVbstbENLuy1si92WtrOJaNlTs0Z1bg8dgk5mNspxOJhb277U7IJte0v7rexCK7tQbBudJA9szSw5StWgFXQyUrEYB3swC7jU7EJr2y04+PaO6NkTLR1tgf0qQSvoZIZz0qBbH9BWdqGV7VZ2oZVt225L21ti261iO1AGW6Olo6tgvyhoBZ3McK5agybS2u6lVGfodd6OLe1GreBDRT+yghbmvS4kOGfKGPK2OKCXml1oZdu229L21rSyvaVdLEjygg8r+JEUtBYdVDCVsUGjurToRNDKLrS23YJWdRa7LW1vxaKliC+lXpbNaeGYpAjslbQ1rTrScbB7qdhuZRda2YUWtrEUtIYfkgKE9+jJC1oLjtl4cym1ubQ4mMKlZhda227BpdjeUbsL/rC3lsfpQUvZWF4n0Sr1qUiDtjyYW9tuZRda2RZ7rWy3sAutbEftsk1SAPaE9+ZRdnvowevkQsVKaHEghRTbvDSN19Q+8cQTuyeffNK8UDGX1Dq7tp955plxTzqptmth28V/6kJ6+umnzbY1sW1jbyvbYldsb8mi3QWf2BvMUVifpDeXPjecaK961au8r1v2uhFwzn1zaSotDiLk2qWJ77///t2DDz5oOjrvyD916tTu1ltv3V111VVjrjilth966KGZ7Re+8IUq263aGlzb1IW+97rXvW56Tzl96fnPf/7uuuuuq+przDZv7wR5bfi1115r1mtwnNrbh3lzqfu6ZYtoCVb5N9xww+706dPjWhrFIy3c8Dq6QuPTqC0OaoldRlTnzp3b3XfffeZEY53gceHChd1tt91mrtgxSm0TsOQkF9ucfLfffvt04odo0dbgqzMnCW312te+1vynLiSW77rrLtOeoRMphZDtRx99dHf33Xcbe4z0xPZrXvOaKrZ9drdA7NawHS2hYt3SghaGLeN7bsj+ig7WbNQUatilIxM0CFhgl0niqsUJ5wYPO08uYpvkO6GwiW37Fse2W2I7FY1dAu2dd94581cggNxzzz0meOSgsU3Awo6bT2wT1FID15Ldtahtl1K8JVG+pIpkjbS8TlZ2rMXBhFp26cyMsEJBQ+BqzaiHuaaatiVg2SeaW/7jjz8+jbhq2M1BY/exxx4zIyxfwJI6Mfohz/nz58c9cUQXs89xI2BRbmweEL+4AJBXw5Ldtahtl5KCpUXslHqQHLT2DOKcsiHIFcopDVq7YTXUtiujHEZYsYAlNkMjrhywR7AkwVKdCJbuiGtNpM6SYlAXRk/4R9Bwta6eAH3vvfeawOVr95DOB3pfwLLLsBP5sR0KXHbeLVnLbrA07Kxcx7w5LXEswblQzhYHUljDtj3KsU8csWUnG0Zcd9xxh/mfizvCWkL8sEd7a+LWOQZtx0iQW6+UgEpeNHbw8LV3DGxzuycBS/RLZWCb20i5TdXq1mBNu95SsaWwV8Oj4on4GDgoySAVG1Lrg7mGbTq73BJCqi2CBp0+Z8QltkkSLG37vmQjtmO3QanE7C1B0IiNAN2y7WSPuFhPBdsEPk3gF8Q2bY9tbmnlOGyF+JBT52ywtWCPvZJqsFrQ2nNwy4Z02OJg0sElYKV0Vts3EqMeJpxTRj0ywpKAJWWlwsjm1a9+dfGIK9e+IKMcO2BJmdqy0Urg0kLbyS1hKFgKMX8I/ClzXKX4fFgd7ClsenMU+lo1aOGKJINUrNDJHEIdag3o7DyDFQpYti9u8iGf7MnzQDGwh93YszMuPj8kYZtRhrYscMsoIee2zMbW2COupfqwX0ZYrm1fWkJsl9zuh0j1pSbG2oJN9krao4K/1YLWnisbN6bQ4kDS4SVo2J0p1xfKYbTDM0CxUQ8nBs9gYZvlEKn+MOIiCGvIqV8I6i0PwWpZqhsBiODBqIfyfbDdDVi52L5QVuoFIIaU24LJ9oL94F6FVktx0MKNmSsVndMiDbr1ARWbjE5STjTB9ttNwFWaeaZQ2dwOSnDxlSEpBcmv+RQstWwN7sjEtecmDXbgcpGAxX7yLeHzwU4ujJY15YaIlb02KbbJ4c2FtrLvWUFLHJy5kuCcLtcyrQ+mwNeRlvwQjZ00yBwXJ5aMpjgReJzCnnTPIebTlVdeOS5dxN6/Ftj1+ZOKXQZJAtfDDz9slmk3LgZ80hebw3LLSQUNX51KJddeKWJXa5tc3pzoV/I/7buHQ9bbXvWq3aP2FSvRMXJfVfDdwxYHEmJ2aRdu5WpNvPps0VYSHAlenGRLhy63rTjJXvayl+1Onjw5btkObnU1H2aU1I125D/tKAGsBq5PrN900027s2fPjlvi5NaplBS7XDA5RkOjjVsclGXdcP31uxs3/+4hziVUlpz63HNoVElbo7HLfr60m3uSi42YLU4ubkOZ46Lj+E40TTlLcDLzJeCrr7563LItfIn2+qFDu/7XqBvQbgR8uaUvCVgxn1inHmfOnBm3hPHptyDXblChLItcJbXNGbeqnYM9B7UVG/K1PpgptglYL37xi72Byy7Pl7T4tHZKwacnYPH2BwIH61vg+sAo6JZbbjE+4I9sT8Euz01afFo3xbjmmmvMhSyUT1tObarbpRxJEdgrqZTiifgYMwcVFYMWB1Iotc2rXjjpefWLlFVSnl1GzXJ8ZbGNEZZvlLMGIT+AYPW85z3P+LKElGOnHGqUAWh57QqvHXKnP2qUn8sqdpXl7eVCNxzjXFYJWjg5OYqDC5VrdTBtu7Vsc1sVGnG5uPbdlIJPL2mJrUZYKT5xwt988817Iy43afFp7ZSCT0/CT96tZY+w7P1bs5ptylsok72SZlTwpWrQmjkZqZhpSPlfoRKprGmXcvkEjFscApdtR+xKysUtJ7csdAQHTrI1R1i5PtojrlS92My1baMpi+28iJDjTpvG8q6J2G1hWwharuRTlaCFK5M7OKZxbuNG3fpg2nNcqXZtX30pBZ/eTjKaYbkWro0SCADctnLL5Zbl2rGTFp/WTRqYw8JPCVhbkurrWmB9zwN8klSJ4qA1c6Vxo/lodTCxyRwXV96lT+LEx1Jf3XJiZbFvjRFWzbIEGXFJ4JKUg63PLUOQMvCL22sC1pbUqEMN8MDrxUq+ZQUtcXJyCeeOQeMJcjC3PqA+u4y0XvKSl8xGXG5KwaeXpIUgIJ/Qpeh85PqQioy4brzxxkU7rk92SsGndxNzWPhFm26BbXtLfHZZ2vOC/ZJWIrmlZ64kOkfO9aqCK9sfTFiyK3Ncqc8+Sbl2ysHWc/LnzhPZSHlbgj0e1sR3CRLih51ySSmH/TKHtUXA0vi0BiG7Xk+U/pXWIq+1cS6xAafcGdoY0qhbH9BUuzLisgOXXYYvpeDTSxJYZlSQO8LylbkVYpcLAPNwNSbn3aSFvFuMsHJ8q8GS3b2t5FP6WKMmq18icHJyVFmxJZYadU1K7DLKkRFXie/ig52WIE/uHJbWxhr4bLMuc1whRGenXNxyGGERONcIWLadrUm2S15lfnJNOQvrtlrQmpyUihU6ehwOZg3b8qniUlm2TTelIBrmgjTzQYLotPlrorHNBYAgHJorTMGnt5ON2K0ZsEK2tiDLNnmV+ck1y5liJ0D1oDVzsoKDB3UwldDx3afm3ZRDqBxOMG3AsnVbInZTbJOXdkzFtpVqj9Edx68GqfZrsrZtSt4rvZK9akFrchLHJGUiDbr1Ad3Sbu6V2vbRTSHYF7KnLWMNatglgLh6u1xf0uLTlQYst7wt2cI2Je+Vjr2KNqsErcmdQsdaH8yt4M0CvGVgCfHLTjnwChbfa39zyyulpC4u0o5SZkm5dhmhcnira86bIWJlrslSfWqyZwGbK9jNDlq4IinVOTvnlo1q08ouHZ6X98n7320/3JSCTy8JeC0zr2Jxt29Fbbu0I4GYlyNqsX3wJQ0ELWxqAldq2bXY0i4WJE1gd0XbyUFr5mCGc5L7Df1guohNXo8sP22V4ofkD6UlGG3xhk7NCK8Gqf6lQMCQVyS7wcO1ayctPq0k4CV4oZ8Ic/NuxdZ2sbJnCdtK+yVe5k6sqJ2zMYpMbQktOhHYHYkOTsAipV6lSbnYZRCwcn9XMYUSf5eg7XgRIm83JRDb9cu1m1oGI1Zed03gErTa2rSwu2cN+wk+lHqbHrQyGgiFadgNG1cO5uYH1GOXE42rM0kClp3Pl1Lw6SW5cMIxQpHb0xos2awJgSL3h1RDKQVbw20+/qSWUYr40MruBMsJPpBzyl3ge5WJ+BCTkwUOptDqYELIrj3CglL/RG+nVBipaH9XMUau/RxoR+awmJtbClh225T6FyuLV2Az4tLOcZXi82ELvHYT/CDnLHdhHVYJWpOTOFfo4BLSoC0PZsi2G7A02GX6Ugo+vST5dZrUwGWXsRW0oz3Csn3wpRR8ejstgW8y4lojcKX4UpOoXaUv5JrlRKfUxqgatCYnKzkXo8WBFDS27YAV68xSlqbMGG45mrIYcWnnuLRlrgH+yS/0lPggdbBTLnYZXAAYAdYKXHbZW1PL7qwEyqtYlypBC3eMS5WdczkOB1Nj2w5YYGvdlIJPLykFWycnnDvisvOQtsS2mzKHZet8KQWf3k4uHHP5VDGHWNlrU8s2akkGyluhPsVBy7i0knPCIR1MTi5+2j3lltCHbTvVBxtNOe6Iy5dnC2z/CAI8ExWbw5L8ti6XGmVxAWBEmPPsWAtq2t4rZcU6ZQUt3DEJxzKc0yikQWs1qpYSu5xoKQHLtuWmFHx6SVpkxMWni1vi85V2ZMTCJDfLdh47peDT20mLTysJZMQVm5x3NVth261hmxIkGShT0ookBy3jToFjS6paDZpKqV1GAwQrgpavs0r5dsqhVjnglsNIa6sHUMWmC23Hp4TyHFYudr18drTklCMjLkaKNill1GQNu3ulJZRf6kn6SCuz8qhMw3n00qi1G3aJmnZ5yv3hhx82y3a5OeX79JJS8Ont5IORVmnA8KGxDZrHGtyyfEmLT2snLT4dIy5uve1tW+H6UpOpRMqWpKSGN1Um4mPgpGk4p2JrNuoSte1yghGwQrcDMex2CPnFNl48x48nvOhFL9qdPXvW/GiGjVsGKRdGWgThWmh9of2w6wasWvVCy/u3eOMo7cj/1FdgCxqfGHHxOEROv8gl5k9VEm2Qe1IU+rda0DJO4twWDahADuYaB5RP3uigIWzbblqCV8rwXnTe1Mm7ozjJeDcWb86UN6BqyrGx7fuS3KLl4paXgnyKmau3dXaiHeUVyfw/ceLEtO57m6urd5MW+kXtUatLjl9FJNgh55QbXQUfqwetycmtGjDCVgeTE0Js2DYl5YCOdzcxqrJ/yAHYx0nHGzT5v4TtC0kD72JPJaX8EKnvq7LrFbLNdn6XkODvls/6mTNnzH45jqFyNIhekt03aiM2jiszzyr6WTVomQaU1Ag5kFseTE5wOn2qTdtXX+Jkir3TXQKXPeLyJS2SnxM59v51mxw7ISjDbUe7fF/SQJkEfzvw27CdgMYt+BI+H+xkw7p7wSklZGttUuySw+Qir6SKVGlN42Rlx1LRNugaSKePjXrEPzuFoDwZYcXysY+ApR1xudi+iB3+82s9S+XZmppQZ3vUkwtayiAQcWu9NIIjL/XmltG2K/WUpIW8lKUN/jFy7NcixS65ppwr+loUtKYKrehgDLGvbdRa2HbFNhPjzDOF3v2uhZMnNsLylUmA8U0q23l9yYVt8ruCLkvaWlB/GfXE7Lj++BLHgrZkWQO2uVhgm2WtDmy7JAJWyY/h2mVtTYptckgyoFnZ56ygVdqYJVootZ/Lkl0C19KIy0XKJMUCluQJgW33VjEF8vtGGpBaVim0A77Yt4r8t1MM9qPlWKTOkfls+7B9cfPRhgS/VNvgK28LQnWJMcuJbiO/k4NWSYOmNoqNaEvs55Bql4Aln+wJdhluElh2bwl9+WJI4NLe2kniROUE51bG3UfaCtsmJ7yMuPBPC1oZYaXqbNsy4rK32ykEAStldAeactegxO6kQJuoL61l0e1hClkNMzZojraUErtMzNujniXIIyMsTrQS277AJeX5ymWdkUXOLVFNfHbxh3aJjXrYLon85NXMYQmidaEsGXlqoAwZYaHVELK9NqV2jRJ9RhmTtoBVg5Y0TkoD5WhqUdM2wWPp4UXs0MHtUU4NbNuxMtlX27YW7NkpBO0TG/UILGvnsHx6H9imPLENttZO2hGWrdmSqnYTyyC3pFStj9WCVmrjVGvQDNayzYiLoMCoR2y4yT0paoFtRhyhW0U5ITnZatteItUevsbmmdjGvqV5JPKV2GbZhwSs0H6xm2q7Bq3sCpNlfKjkR/WgldJIkrdFo25lW+a43OCBXXcOqzaMuAhc9uQ8SeaL1rRtY9vOtUdAoL0kyNqJgEJg8QUNO18ulOsL8CyzDdtu+WLT3b4FLW0LWDbW8aGyH5c9l/DFKLLeeccd3vcFpTTQVcMJzO2Ldt6hFi0PIl/l4Ht1fE2Fk4DOzu3MFmD7woULM9t8B29t1mhv+iCvrZGvGRGQGam6rGWbvs+XoIE2dEd/LfvYFrbpQ6E3mcDkwYIv9EEumjkUB62chtoyaB23TiTNvaZfobLXtr1lW/vqspV91/aW9bZpYTcUtCZPlD6VBK2s20MaS9JxpZV/S22zpl9LZa9te0ukLnbailZ2hVZ2wbXLmtnC9o18Sg9ahY6ZBh+XayMHc+sD2soutLJt221pe2ta2W5lF3y2WTJrbMvwqWQyvfpEfBCn0jVxG3QrWtmF1rZb0KrOYrel7a2J1XnakumXKdPzoYmW1YPWVPlxvRZTuTTAhhwHu5eq7S1pZRda2VbbZX+ib+Q2KVHnY72gpal8IupGXYFWdqGl3UvNdq9zfUzJlF/JRv2gNTZArSaQBl2zUUO0sm3bbWl7a1rZPg52W9peC0o25Ve2UTdo0QjjYilrN2gIsdvCNrS028K22G1pe2ta2YUtbFO6sbCSnSpBSxqi1MWpnJUqG6KVXbBtb2m/1C7P6UjiPej8GAY/mfXII49Mief5eBCUhzHJI/kh124pJXUuoZVd2Mo2pRsbklYi/eHSO+/cPSYPl9IQR0tJ8HApbyKQh0tbHEhoZRcOzTbHnp8X4+FC/kuSH26QMu3u5Nphne9Ectz5ihHL8n/N9liz7CVa2V7Lru/h0slSgk2+xcADpjlkBa3Hh6tnNkPFTgwd9fkNvsYDvQPrka7BaImRk/ziUEKX8WL7wzL9gK/DyJeSa7RTq7aGVra3sOsGrVybJUEr+fYwu1mo3JDaHM6jxm3ZmVrYzrXLyIlbPTrnPffcY/4zqqKjpgYs8cFONpRH2Xwv8957751+Ul4CZCo+G1vRynYLu1jLtVnq63qPPAg4SKOyeLRlM+Rgbn5ALbstbadCACFY8QvP/MQ/I6yl3+yz7flSKsx/8cO3+MCXvDVBssReKa1st7ILxmam3Rr+rhu0aFT+Ha1tQsuDCS3sltZZRlb8GjLBSkZVgl2+m3JZKkveTMFP9Nu3pkJItwXHwfbWlNa5pt/1gxaOjQ5u2bQ1GyUFsbu1/Vo2CQbckhGs5JUrdtkl5bvl2EkLcygELwIqn1BCir4WOb7XopXtGnbX8Ltu0Bqd26ppazRqLq3sQg27jKQYwXAbViMYSHvYKYdQOQRXApfMr22F7cOWuPXfklp21/K9PGjh2FhJXFy7iaVB12qQGK1s23Zr2OakJ1gx+b00ZyW4PrgpBZ9e0hIy58Z/zXxXDin+1KSVXahlG/XadSgLWjjHv6M1PYkVqtWgObSyvYZdmWjndtA3WrFtuikFn95OKYT0TNYTdHM/ZbTxlb8VrexCTduUYMrZoB7JQUscM5UdtyWRUKlaDZqK2G1puzZyOyi3V3YdS22uVU6sLOrDp5vUpyRwxWysyVL91qS2bVPWhnXJGmlluTdWDG1MLw1as1E1tLILa9vmFpBRibwmO9WO7Z8vafFp7aTF1jBRz8hRPkRYwtaStqSVXVjDdqu61J2I90GlxorFqteqAVrbXdu2BCxGJdo5INu3Ev+2KoeRFreL8oGCj5B2C1rZFru1bVNai/oI6UErxdkxr6mkWbqI3aBbN8ClYpeTmdsn5rEE24dQ0uLT2ikFn16SBoIzdZVf6YHUMmrSyvYmdiuUXeLfOiMtHBqdWrHpkln9YAZoYZdRFc83MfoQ+6U+2OWUlOWWk1uWWwZ1ZlTJLWMLbF+2ppXdFtQNWjQajcfimFrTqiO1sgsy6W6POjTYPvuSFp/WTlp8Wjv5oO6MuLYKXEv+rEkL27QvqSXJQcvbPDTa2HDbH7p9WhxMobVdEsGKOawlbA0pl+NWjnwFSPsMWg6lPpbQyrbYbVVvoWykhfNUgsUxtaJlg7ay7bPLp2jyRWN7vy9p8WntZBPbB+5+O2nxad3EYx1MztcMXHb5W9PKdiu7MfKCFhUYK5FVlYoN0LJBW9kO2WXinTkdCVi5SPkhOwLvvuJdWCdOnDDvwrrxxhtNOnPmzO7s2bO706dPm3Xem8TP1/OyPzSpaP1xYT4v5VPTEKl2a5FT51q0sqsh+SWAd999t3neJ6s6YyPwtkr7zaWptGzMVrY1dhlZ2J8UhiitA8Hn5MmTJlhxDLXHkVEPIyASt7Ay71SzTd2yWOeFc6dOnRq36KjpUwqt7ILGNhcC+lnphYCfxE89JkLWnFZys9IYY4Nk6Udo1BYHVey2tB2DDsSFJBSwbP+XygqBjosNI6ebbrrJBAIJWloYZcmojNEYiRFYrk9g18tXjrSNdmI+VM7atLbbwnYuOWP1cUEBecf8/M1pllaN2soupNpm5MJtkK2zkxaflsTIimDF7R5XR7aVQhkEMLmlZNmH7YcvaWCEx6epodFBanm1uNTs1iJrpKVibBD+uhrNwLJVo7a2m2pbRhI5E862TZ9dtjESYkTE7WDu7XwMRl/YkLkvbMR80mKXQZLALtj7tubQ7ZbeGpaSNxEfggaRxKr562GotFtxadBaDZvCIdslWIW+vmKX70sx2E8QIZjUDFYhHwhe3HJq7bnluMmHtFNo/5os+bYWte3WClgl/qSPtELGrO0saV2q2aCptLJd067diaTc0vIJGtwKErRq+Qmaspg3Y2Tn3i7a9SrxactRQg1/c2lldwvKghbLklgdk4bWB3Nr22vZJcCk/m6g7YubGPHccMMNwTmmFNyytYgPMn+WorXtuYk6Ufaa2Pa2ZgvbBznSGqxdTCMs5buwPlsczBBr26VsPj4mcIUQH5Z8IQD6RjmpLNnRILeLSx+L2/UK2WQ782a5v7OnIWZ/bVrazqHU36KRFktJptEOiVi99jBdGqakcXLZ2rY8imD/2GmqD+RbCn4xUu1poCwCjT3ictMSBGLqRdLkTyHFj5rYdre2veXtdYjkoHW5NNa4rmajxm1xIIWWtgk2nJjySZ/mNkj85cSOPXYQY+06UzYjLuqUAnUiiDM3V/osmMvadQ7Ryq5NraBVcpuePtJKMUYDS2J1TEPNq0ZsOZhbH1Db7nGxLcFLnqnyjbwkAf8ZzWgDlq+MLdD6SH3lK0VoSk4OmxZ1hlZ2QxzknJZpwHE5iuOUvWYqXqHyrQ5mK7ugsc1+CV6cvKFnrMjHia0dxbSsM8En9gEB+93nvUrBrqStaWV3icO8PYxduWhkSSMs+Zo+t+qtOlIru1Bim/kuef6J/5zMlMN/glpsotu2m2O7BJ9dlvGZ4CTQHwlSjCzZlzsnJ/jsbkVL21pqvDWjtH5JX5iG8w8/bH53bk/mOLLk1tmbbjLDeA0tD2Ir22va5djFyj+E9uaNFtRDgnAph1Dn4wBfltb+iEgIjhlzr7m37ukjLXfYTYNbjc6S5hAs/ewTB1LS1rSyvZVdX/lb2Xax7abYpuMzqirxN8duLVrazoWLROIYx0tpnZODFp3FgGHLOEsprsiV0qXlgWxtuwWt6ty6rVvWuVW9S+F8rXV7WNIGeUHLMag2j25M9qtCWh1M2+6lYruVXWhlF1rZbmV3DWqNtHJvC4VktT3RyWFQHwrnoK3xAjgtLTtRa9stuBTbu2Wd14JR1kGOtIiSVw6jLbVJnLMcZMmsDRG7dEIvBWmoFh2plW3b7pa2W9kVWtluZXcreEvGQY60QPVcDwfOOXj2GpV/4vHHqzRCjFadqGUHbmUXWtptVe9WdrfEnK+JP0kXYpoXzyQraPGoQvAgsd3Zx5ovN43wzNNPj2v1kE60dUdqZRda2bbttrS9NS1tt8Ccqwuf+GugvUqfpas30uLgeQ5g7JCaX48Zf+6qBq06UcvO29p2C1rVWey2qncrmMfS/GCKBtquyUjriiFS8qT1hHMQWZO0BI3B64JzadWRjoPdS9X2lrSye1xgQMH79XlddQ2IG03mtDiA5qsUHEjrYLKkPrSjlkbhx0VT7pdbdqRWdqGl3UvNdss6Hxc4N3mvfq25LEh9W4eP/KBlGefQqg8vHcHpDNwmPvjAA9GfeZJO1KIjtbJt221pe2ta2W5l97jCp/v2j4GUYp48KJzPguxxmnntCQd4XF+EjuDpDGwhcd/8wLlze/fOrTpRyw7cyi60rnNL250jZITFHVCt+WYgYJXeGkJ2CdybXnOt4vW1dAZPh2CLu5X7ZkZcF86fN+tbdyTpvC06cCvbtt2Wtrempe3jDIMHvhQd+53IXGq9jDH5LQ82PGx29113hZ+S9TiocZmKnRgqyKMV9qt216Jlx21lu9e5IxACOIeZu6r1aIMLnxiWvNnBpihoIeU1NY8Mw8iJQMdQdxdHTyWZvCMRqZcqre2Y/aTdlkvV9nEgdIoTnJhHZu6K/wWhIArtzwsc7feglVAUtIAKM9qKFaLqMpGOZe8haPF6HObTWOb1zzSKnWD6b/5aWHk0RHMmlAOz3InaHFLqGaPE763rLNSq+yEhp7J9SrPsJkZVJJa3QH54pcYoC4qDFpw7d253/vz5WSOou0ygc6n0Cx0zundFrRDMVaJXamEvZ4IWZrkTtVCqh0lVYr/Edqa2cwQXj5qjLKgS+rhXFac4xKrDTGcIdIhFfUQL7AnuLdFCRCsEy1iwLXj1Si3s6RO04NUnMNMn2hamMjL0JVrBjNQytZ2LMCddM2BBlaDFsM8M/zQHOdIZ2LpYQsTGon7Bv0WtQu/NodCCV6/UCl59ArPcibZhT58ICqPKsA0lWiBYXYq3lmvAIw7aV6qnUOX2UOAWkWetvEVGOsJiF1noRNG9ig54bPUKLXhzKbWwlzNBK0yKDC1U0WdqoVTfmUPgZyBT+mvlPurMjI3wayj8MsoMOkKgM7B1sZssdKToXoV2NX2k3jbeHEot7OVK0IJXnwC5J0WiFiZ9ot/CTJ+B0Wfa7vihPYkFawQsqDrSAh4QZbTFE7WhglXdI9KJFvULHTC6V9l5vblKtLCBbWGWO1EL1fQZWjCqTC2U6jt+CFjcEkZfX1VI9aAFPP9x3333eb9ouViNhYpG95ZoQdHIwRzKA+TNlXBw93ImaKGqPlErTKoMfYkWjCpT24lDkGLinbuttQIWrBK0gIfVeBSC7xKq3I9UclGvaKBgDmXjluhLbYM3Z4k+QQuz3IlamBQZWjCqTK2w5onUOXo56NoBC1YLWsCI69z995vAFTSzUMHoXkXjHFu98sB6cym1sJczQStMigwtVNFnaqEHq3WhfSVgbcGqQQsIXHxbnF+mnn1HcaEjLXazEr2iEwdzKE8Aby6lFkr1MMu9oRYmRYZWMMoSfYG2o4PHna677jrzLNZW7b160AJM8P1Evj3+9MKXMRervdAw0b3KRg3mKtFvoBVmuRO1MCkytHAs9Jnajh6ew+JTwtlbjDdgk6AlMM/10EMPeV+vvNjFFjphdK+iAwdzKDu/N1fiibOXu0S/sW2YFBlaMKpMLfSR1TbI7aB5p16l7xOmsGnQAszxOASjLvOm0iXzio4YzKHsxKvoE06g6voELcxyJ2phUmRooVQPPWCtD23MqIrbwRpvIM1l86Al8DwXE/Q8Rc/PiO25sdAJo3sVHbhUD95cCSfPXs4ELVTVJ2phUmRowagytVCq7+iQYMXIigdGW18gmgUt4blnn909MtwuPjYkAtkzildmRJtM0aDBHMqDsYo+sSPMcm+ohUmRoRWMskRfoO0sQ/ty60ew4l12/D8ubd48aAm4wYiLiXpuH3krKreP8onjYnMpGjSYQ3kwvLkSDuRezsROUFWfqBUmVYa+RAtGlantLEOQ4raP0RRBivXS3yhcg2MTtHzgmgSvp0lDQHt2SGx/FreHhPN2FXzLwW6uPAFK9bCXM/Hkq6rPOPEnRYYWqugztZci9qhIlu3/JAKSBCrScQxQPo510LJxg5EkWbf/C+56K/ZONatDdZaxT8DOMm572cHK9//QOJig1el0OrD9QxadTqdTQA9anU7noOhBq9PpHBQ9aHU6nYOiB61Op3NQ9KDV6XQOih60Op3OQdGDVqfTOSh60Op0OgdFD1qdTueg6EGr0+kcFD1odTqdg6IHrU6nc1D0oNXpdA6KHrQ6nc5B0YNWp9M5KHrQ6nQ6B0UPWp1O56DoQavT6RwUPWh1Op2DogetTqdzUPSg1el0DooetDqdzkHRg1an0zkoetDqdDoHRQ9anU7noOhBq9PpHBQ9aHU6nYOiB61Op3NQ9KDV6XQOih60Op3OQdGDVqfTOSh60Op0OgdFD1qdTueg6EGr0+kcFAcZtD7jMz5jd9lll+0++ZM/eff000+PWy9uf//3f//dI488Mm7d7V7zmtfsPvETP3F3xRVXmP1v+7Zvu/ujP/qj3XPPPTfmmPOP//iPu2uvvdbk/c7v/M5x62732te+dveyl73MbMeWzR/8wR/sLr/8crOP9MM//MNm+8/+7M+adfaRR6Bctl911VW7v/iLvxi3XkTqYqcTJ07sPu/zPs/Ux+aJJ57YffiHf/iU723e5m12999//8zfUGI/+UDbTvfcc4/xA3/I96IXvWj38z//87tnnnlmzDEn5Ae67/u+79s9/vjjY85w22tsSpu6iXr8yq/8yl49NPWVMt/jPd5j9/DDD49bL26320+OmWwL1fvMmTO7r/3ar909+uijRgehetvYeXzp13/918ecu93//d//mT6h7fOHxEGPtH7nd35n9z//8z/jmp+/+7u/273d273d7hd/8Rd3zz77rNn2n//5n7v/9//+3+67vuu7qh3EV77ylbOy/v7v/978f8/3fM/dLbfcYvb98R//sdlGkPmzP/szs/wO7/AOJshoeP3rX7/70R/90d37vd/77V7+8pePW3cmQL/61a8e13a7O++8c3fXXXeNazq07XT77bfv3vd939f4gT9w99137z7lUz5l94Vf+IXTNg3ovvRLv3T3BV/wBbPA5VJqk3p83Md93Kweqf2C/H/91389rpXx0EMPmcD00R/90bNAWAt8ffd3f/fdb/3Wb63a51tx0EGLg//Lv/zL49o+dAhOCvIBV7gXvvCFZhm+4Ru+Yfc3f/M341oZdAobgghX0pe+9KW793qv9zLb/vIv/9IEGE64f/qnfzLbPuiDPsj4FQOfX/KSl5irLLzqVa/afcVXfMV0or/uda8zIxHhwoULxgZXWeyjZWTCaA/4zzrb2Y+fmnZiVMsIQQIm/tjlElTsq70PRpauPz/1Uz+1+8mf/Emz7JJr8/nPf76xw3/h27/9281xyukXnOj4aI/sU5B6k1iGP/zDP9x9z/d8j1lOhXaQ8iSdOnVqsW7f8i3fMvW9Q+Wggxb8wi/8wu7ee+8d1+b8+7//uxlSw8d//MebfJzMP/3TP222PfXUU9PopwRO+v/6r/8a14647bbbTIC68sordx/4gR9otuEPo6D/+I//MEGGfR/6oR9q9oUgqFCHO+64w4ye3ud93sds/9M//VNzCwAEyAceeMAsC//93/+9e8ELXrD78z//c6NlRCpa/rPOdvbji6adyP8nf/InZtu7vMu7mDpyi/X7v//75raNE9t3G2bDLT3loPurv/qr3TXXXGO2c/Gxb5eEHJuU+du//dtGS12+/uu/3mznRObCkdsvNCP7EFJv0r/9279NgeSXfumXzK18KgRWKU/SB3zAB8zq9rEf+7Gmz5B+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";
	            return new GraphicsResponseDTO(base64);
	        }
	    
	    if (req.getChartType() == ChartType.PIE) {
	        chart = buildPieChart(req);
	    } else if (req.getChartType() == ChartType.AREA || req.getChartType() == ChartType.STACKED_AREA) {
	    	DefaultCategoryDataset ds = buildCategoryDataset(req.getSeries());
            chart = buildAreaChart(req, ds);
        } else {
	        DefaultCategoryDataset ds = buildCategoryDataset(req.getSeries());
	        chart = buildCategoryChart(req, ds);
	    }

	    String base64 = chartToBase64(chart, req.getWidth(), req.getHeight());
	    return new GraphicsResponseDTO(base64);
	}


    /* ===========================================================
     *  DATASET
     * ========================================================= */
    private DefaultCategoryDataset buildCategoryDataset(List<SeriesDTO> seriesList) {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        for (SeriesDTO serie : seriesList) {
            serie.getData().forEach((category, value) ->
                ds.addValue(value, serie.getName(), category)
            );
        }
        return ds;
    }
    
    private JFreeChart buildPieChart(GraphicsRequest req) {
    	NumberFormat percentFormat = NumberFormat.getPercentInstance();
    	percentFormat.setMinimumFractionDigits(1);
    	percentFormat.setMaximumFractionDigits(1);

    	
        SeriesDTO serie = req.getSeries().get(0);
        DefaultPieDataset ds = new DefaultPieDataset();
        serie.getData().forEach(ds::setValue);

        JFreeChart chart;
        if (req.isThreeD()) {
            chart = ChartFactory.createPieChart3D(
                req.getTitle(),
                ds,
                req.isShowLegend(),
                true,
                false
            );
            PiePlot3D plot = (PiePlot3D) chart.getPlot();
            plot.setStartAngle(290);
            plot.setDirection(Rotation.CLOCKWISE);
            plot.setForegroundAlpha(0.6f);
            plot.setLabelGenerator(
            	    new StandardPieSectionLabelGenerator(
            	        "{0}: {1} ({2})",
            	        new DecimalFormat("#,##0"),
            	        new DecimalFormat("0.00%")
            	    )
            	);
            plot.setBackgroundPaint(Color.decode(req.getStyle().getBackgroundColor()));
        } else {
            chart = ChartFactory.createPieChart(
            		req.getTitle(),
                    ds,
                    req.isShowLegend(),
                    true,
                    false
                );
                PiePlot plot = (PiePlot) chart.getPlot();
                plot.setBackgroundPaint(Color.decode(req.getStyle().getBackgroundColor()));

                // Agregar porcentajes en etiquetas
                plot.setLabelGenerator(
                    new StandardPieSectionLabelGenerator(
                        "{0}: {1} ({2})",
                        new DecimalFormat("#,##0"),
                        new DecimalFormat("0.00%")
                    )
                );
            }

        // Subtítulo
        if (req.getSubtitle() != null && !req.getSubtitle().isBlank()) {
            Font subtitleFont = new Font("SansSerif", Font.PLAIN, req.getStyle().getSubtitleFontSize());
            chart.addSubtitle(new TextTitle(req.getSubtitle(), subtitleFont));
        }

        // Antialias y fuente del título
        chart.setAntiAlias(true);
        chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, req.getStyle().getTitleFontSize()));

        return chart;
    }
    
    private void logDataset(DefaultCategoryDataset ds) {
        System.out.println("=== Dataset Info ===");

        List<?> rowKeys = ds.getRowKeys();
        List<?> columnKeys = ds.getColumnKeys();

        System.out.println("Series (row keys):");
        for (Object rowKey : rowKeys) {
            System.out.println("  " + rowKey);
        }

        System.out.println("Categories (column keys):");
        for (Object colKey : columnKeys) {
            System.out.println("  " + colKey);
        }

        System.out.println("Values:");
        for (Object rowKey : rowKeys) {
            for (Object colKey : columnKeys) {
                Number value = ds.getValue((Comparable<?>) rowKey, (Comparable<?>) colKey);
                System.out.println("  (" + rowKey + ", " + colKey + ") = " + value);
            }
        }
    }



    @SuppressWarnings("serial")
    private JFreeChart buildCategoryChart(GraphicsRequest req, DefaultCategoryDataset ds) {
        CategoryAxis domain = new CategoryAxis(req.getCategoryAxisLabel());

        logDataset(ds);

        // Rotar etiquetas si son largas
        @SuppressWarnings("unchecked")
        boolean shouldRotate = ds.getColumnKeys().stream()
            .anyMatch(key -> key != null && key.toString().length() > 10);
        if (shouldRotate) {
            domain.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 4));
        }

        NumberAxis range = new NumberAxis(req.getValueAxisLabel());
        CategoryItemRenderer renderer;

        String[] palette = {
            "#4e79a7", "#f28e2b", "#e15759", "#76b7b2",
            "#59a14f", "#edc948", "#b07aa1", "#ff9da7",
            "#9c755f", "#bab0ab"
        };

        switch (req.getChartType()) {
            case BAR:
                if (req.isThreeD()) {
                    renderer = new BarRenderer3D() {
                        @Override
                        public Paint getItemPaint(int row, int column) {
                            int index = column % palette.length; // color por categoría
                            return Color.decode(palette[index]);
                        }
                    };
                } else {
                    renderer = new BarRenderer() {
                        @Override
                        public Paint getItemPaint(int row, int column) {
                            int index = column % palette.length; // color por categoría
                            return Color.decode(palette[index]);
                        }
                    };
                }
                break;

            case STACKED_BAR:
                if (req.isThreeD()) {
                    renderer = new StackedBarRenderer3D() {
                        @Override
                        public Paint getItemPaint(int row, int column) {
                            int index = column % palette.length; // color por categoría
                            return Color.decode(palette[index]);
                        }
                    };
                } else {
                    renderer = new StackedBarRenderer() {
                        @Override
                        public Paint getItemPaint(int row, int column) {
                            int index = column % palette.length; // color por categoría
                            return Color.decode(palette[index]);
                        }
                    };
                }
                break;

            case LINE:
                if (req.isThreeD()) {
                    // No hay LineAndShapeRenderer3D, usamos normal
                    renderer = new LineAndShapeRenderer() {
                        @Override
                        public Paint getItemPaint(int row, int column) {
                            int index = row % palette.length; // color por serie
                            return Color.decode(palette[index]);
                        }
                    };
                } else {
                    renderer = new LineAndShapeRenderer() {
                        @Override
                        public Paint getItemPaint(int row, int column) {
                            int index = row % palette.length; // color por serie
                            return Color.decode(palette[index]);
                        }
                    };
                }
                break;

            default:
                throw new IllegalArgumentException("Tipo de gráfico no soportado en buildCategoryChart: " + req.getChartType());
        }

        StyleDTO s = Optional.ofNullable(req.getStyle()).orElse(new StyleDTO());
        String backgroundColor = s.getBackgroundColor() != null ? s.getBackgroundColor() : "#ffffff";

        CategoryPlot plot = new CategoryPlot(ds, domain, range, renderer);
        plot.setBackgroundPaint(Color.decode(backgroundColor));
        plot.setRangeGridlinePaint(new Color(220, 220, 220));
        plot.setOutlineVisible(true);

        Font titleFont = new Font("SansSerif", Font.BOLD, s.getTitleFontSize());
        Font subtitleFont = new Font("SansSerif", Font.PLAIN, s.getSubtitleFontSize());

        boolean showLegend = req.isShowLegend() && ds.getRowCount() > 1;

        JFreeChart chart = new JFreeChart(req.getTitle(), titleFont, plot, showLegend);

        if (req.getSubtitle() != null && !req.getSubtitle().isBlank()) {
            chart.addSubtitle(new TextTitle(req.getSubtitle(), subtitleFont));
        }

        chart.setAntiAlias(true);

        return chart;
    }

    private JFreeChart buildAreaChart(GraphicsRequest req, DefaultCategoryDataset dsOriginal) {
        CategoryAxis domain = new CategoryAxis(req.getCategoryAxisLabel());

        @SuppressWarnings("unchecked")
        boolean shouldRotate = dsOriginal.getColumnKeys().stream()
            .anyMatch(key -> key != null && key.toString().length() > 10);
        if (shouldRotate) {
            domain.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 4));
        }

        NumberAxis range = new NumberAxis(req.getValueAxisLabel());

        // Usar el dataset original, NO modificar la estructura
        DefaultCategoryDataset ds = dsOriginal;

        // Paleta de colores
        String[] palette = {
            "#4e79a7", "#f28e2b", "#e15759", "#76b7b2",
            "#59a14f", "#edc948", "#b07aa1", "#ff9da7",
            "#9c755f", "#bab0ab"
        };

        CategoryItemRenderer renderer;

        if (req.getChartType() == ChartType.AREA) {
            renderer = new AreaRenderer() {
                @Override
                public Paint getItemPaint(int row, int column) {
                    int index = column % palette.length;
                    return Color.decode(palette[index]);
                }
            };
        } else if (req.getChartType() == ChartType.STACKED_AREA) {
            renderer = new StackedAreaRenderer() {
                @Override
                public Paint getItemPaint(int row, int column) {
                    int index = column % palette.length;
                    return Color.decode(palette[index]);
                }
            };
        } else {
            throw new IllegalArgumentException("Tipo de gráfico no soportado en buildAreaChart: " + req.getChartType());
        }

        StyleDTO s = Optional.ofNullable(req.getStyle()).orElse(new StyleDTO());
        String backgroundColor = s.getBackgroundColor() != null ? s.getBackgroundColor() : "#ffffff";

        CategoryPlot plot = new CategoryPlot(ds, domain, range, renderer);
        plot.setBackgroundPaint(Color.decode(backgroundColor));
        plot.setRangeGridlinePaint(new Color(220, 220, 220));
        plot.setOutlineVisible(true);

        Font titleFont = new Font("SansSerif", Font.BOLD, s.getTitleFontSize());
        Font subtitleFont = new Font("SansSerif", Font.PLAIN, s.getSubtitleFontSize());

        boolean showLegend = req.isShowLegend() && ds.getRowCount() > 1;

        JFreeChart chart = new JFreeChart(req.getTitle(), titleFont, plot, showLegend);

        if (req.getSubtitle() != null && !req.getSubtitle().isBlank()) {
            chart.addSubtitle(new TextTitle(req.getSubtitle(), subtitleFont));
        }

        chart.setAntiAlias(true);

        return chart;
    }





    /* ===========================================================
     *  PNG → Base64
     * ========================================================= */
    private String chartToBase64(JFreeChart chart, int w, int h) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            BufferedImage img = chart.createBufferedImage(w, h);
            ImageIO.write(img, "png", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception ex) {
            throw new RuntimeException("Error al generar gráfico", ex);
        }
    }
}