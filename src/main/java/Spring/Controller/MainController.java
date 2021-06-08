package Spring.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {


    @GetMapping
    public String getMainPage(){
        return "main/main";
    }

    @GetMapping("/employeeRoom")
    public String getEmployeeRoom(){
        return "employee/main";
    }

    @GetMapping("/userRoom")
    public String getUserRoom(){
        return "user/main";
    }


}

