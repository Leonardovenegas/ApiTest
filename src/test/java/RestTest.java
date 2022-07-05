import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;

public class RestTest {

    @Test
    public void loginTest(){
        String response = RestAssured
                .given()
                .log().all()
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "    \"email\": \"eve.holt@reqres.in\",\n" +
                        "    \"password\": \"cityslicka\"\n" +
                        "}")
                .post("https://reqres.in/api/login")
                .then()
                .log().all()
                .extract()
                .asString();
    }

    @Test
    public void getUsers(){
        RestAssured
                .given()
                .log().all()
                .contentType(ContentType.JSON)
                .get("https://reqres.in/api/users/2")
                .then()
                .log().all()
                .statusCode(200)
                .body("data.id", notNullValue())
                .body("data.id", equalTo(2));
    }
}
