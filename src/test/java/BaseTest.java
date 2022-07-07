import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.Filter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeClass;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseTest {

    @BeforeClass
    public static void setUp() {
        RestAssured.requestSpecification = defaultEspecificacionSolicitud();
    }

    private static RequestSpecification defaultEspecificacionSolicitud() {
        List<Filter> filtros = new ArrayList<>();
        filtros.add(new RequestLoggingFilter());
        filtros.add(new ResponseLoggingFilter());
        return new RequestSpecBuilder().setBaseUri("https://reqres.in")
                .setBasePath("/api")
                .addFilters(filtros)
                .setContentType(ContentType.JSON)
                .build();
    }

    public ResponseSpecification defaultEspecificacionRespuesta() {
        return new ResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_OK)
                .expectContentType(ContentType.JSON)
                .build();
    }
}
