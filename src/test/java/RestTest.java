import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static io.restassured.path.json.JsonPath.from;

public class RestTest {

    @BeforeTest
    public void setUp() {
        RestAssured.baseURI = "https://reqres.in";
        RestAssured.basePath = "/api";
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .build();
    }

    @Test
    public void loginTest() {
        given()
                .body("{\n" +
                        "    \"email\": \"eve.holt@reqres.in\",\n" +
                        "    \"password\": \"cityslicka\"\n" +
                        "}")
                .post("login")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("token", notNullValue());
    }

    @Test
    public void getUsers() {
        given()
                .get("users/2")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("data.id", notNullValue())
                .body("data.id", equalTo(2));
    }

    @Test
    public void deleteTest(){
        given()
                .delete("users/2")
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);
    }

    @Test
    public void putTest(){
        String nombre = given()
                .when()
                .body("{\n" +
                        "    \"name\": \"morpheus\",\n" +
                        "    \"job\": \"zion resident\"\n" +
                        "}")
                .put("users/2")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .jsonPath().getString("name");
        assertThat(nombre, equalTo("morpheus"));
    }

    @Test
    public void patchTest(){
        String trabajo = given()
                .when()
                .body("{\n" +
                        "    \"name\": \"morpheus\",\n" +
                        "    \"job\": \"zion resident\"\n" +
                        "}")
                .patch("users/2")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .jsonPath().getString("job");
        assertThat(trabajo, equalTo("zion resident"));
    }

    @Test
    public void getAllUsersTest(){
        Response response = given().get("users?page=2");
        Headers headers = response.getHeaders();
        int codigoEstado = response.getStatusCode();
        String body = response.getBody().asString();
        String contentType = response.getContentType();

        assertThat(codigoEstado, equalTo(HttpStatus.SC_OK));
        System.out.println("*************************************************************");
        System.out.println("*************************************************************");
        System.out.println("Body: " + body);
        System.out.println("Codigo: " + codigoEstado);
        System.out.println("Tipo Contenido: " + contentType);
        System.out.println("Headers: " + headers);
        System.out.println("*************************************************************");
        System.out.println("*************************************************************");
        System.out.println("Fecha: " + headers.get("Date"));
    }

    @Test
    public void getAllUsersTest2(){
        String response = given()
                .when()
                .get("users?page=2")
                .then().extract().body().asString();
        int pagina = from(response).get("page");
        int paginas = from(response).get("total_pages");
        int idPrimerUsuario = from(response).get("data[0].id");

        System.out.println("*************************************************************");
        System.out.println("*************************************************************");
        System.out.println("Pagina: " + pagina);
        System.out.println("Paginas: " + paginas);
        System.out.println("Id 1er Usuario: " + idPrimerUsuario);

        List<Map> usuariosIdMayorA10 = from(response).get("data.findAll { user -> user.id > 10}");

        List<Map> usuario = from(response).get("data.findAll { user -> user.id > 10 && user.last_name == 'Howell'}");
    }

    @Test
    public void createdUserTest(){
        String respuesta = given()
                .when()
                .body("{\n" +
                        "    \"name\": \"morpheus\",\n" +
                        "    \"job\": \"leader\"\n" +
                        "}")
                .post("users")
                .then().extract().asString();
        Usuario usuario = from(respuesta).getObject("", Usuario.class);
    }

    @Test
    public void registerUserTest(){
        SolicitudCrearUsuario solicitudCrearUsuario = new SolicitudCrearUsuario();
        solicitudCrearUsuario.setEmail("eve.holt@reqres.in");
        solicitudCrearUsuario.setPassword("pistol");
        RespuestaCrearUsuario respuestaCrearUsuario = given()
                .when()
                .body(solicitudCrearUsuario)
                .post("users")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .contentType("application/json; charset=utf-8")
                .extract()
                .body()
                .as(RespuestaCrearUsuario.class);
        assertThat(respuestaCrearUsuario.getId(), equalTo("4"));
        assertThat(respuestaCrearUsuario.getToken(), equalTo("QpwL5tke4Pnpja7X4"));
    }
}
