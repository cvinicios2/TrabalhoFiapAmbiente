package br.com.fiap.scj;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;

public class IntegrarFilasRouter extends RouteBuilder {

	@Value("${route.fiap.input.endpoint}")
	private String endpointInput;

	@Value("${route.fiap.output.endpoint}")
	private String endpointOutput;

	@Override
	public void configure() throws Exception {

		from(endpointInput).tracing().log("input >>> ${body}").doTry().process(new Processor() {
			@Override
			public void process(Exchange exchange) {
				exchange.getIn().getBody();
			}
		}).to(endpointOutput).process(new Processor() {

			@Override
			public void process(Exchange exchange) throws Exception {

				BufferedReader csvReader = new BufferedReader(
						new FileReader("/home/osboxes/Downloads/201901_BolsaFamilia_Pagamentos2.csv"));
				String row;
				int lidos = 0;
				int qtde = 0;
				double valor = 0;
				int erros = 0;
				String estado = '"' + exchange.getIn().getBody(String.class) + '"';
				while (((row = csvReader.readLine()) != null) && lidos < 100) {
					try {
						lidos += 1;
						String[] data = row.split(";");
						String est = data[2];
						String vl = data[7].replace(',', '.');
						String vl2 = vl.replace("\"", "");
						if (estado.equals(est)) {
							qtde = qtde + 1;
							valor = valor + Double.parseDouble(vl2);
						}
					} catch (Exception e) {
						erros += 1;
					}
				}
				csvReader.close();
				System.out.println("estado: " + estado + "qtde: " + qtde + " valor: " + valor);

				FileWriter csvWriter = new FileWriter("/home/osboxes/Downloads/" + estado.replace("\"", "") + ".csv");
				csvWriter.append("Estado");
				csvWriter.append(";");
				csvWriter.append("qtde");
				csvWriter.append(";");
				csvWriter.append("Total");
				csvWriter.append("\n");
				csvWriter.append(estado);
				csvWriter.append(";");
				csvWriter.append(String.valueOf(qtde));
				csvWriter.append(";");
				csvWriter.append(String.valueOf(valor));
				csvWriter.append("\n");

				csvWriter.flush();
				csvWriter.close();

			}
		}).log("output >> ${body}").doCatch(RuntimeException.class).setBody(constant("${body}"))
				.log("Message to be sent: ${body}").log("Erro!").end();
	}
}
