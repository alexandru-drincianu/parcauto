package parc.auto.parcauto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Controller
public class AppController {
    @Autowired
    private UserRepository repoUser;
    @Autowired
    private CarRepository repoCar;

    @GetMapping("")
    public String viewHomePage(Model model) {
        List<Car> listCars = repoCar.findAll();
        model.addAttribute("listCars",listCars);
        return "index";
    }

    @GetMapping("/register")
    public String showSignUpForm(Model model) {
        model.addAttribute("user", new User());
        return "signup_form";
    }

    @GetMapping("/home_page")
    public String viewHomePageLoggedIn(Model model1, Model model2) {
        List<Car> listCars = repoCar.findAll();
        model1.addAttribute("listCars",listCars);
        String currentUserName = "";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            currentUserName = authentication.getName();
        }
        if(currentUserName.equals("admin@gmail.com")){
            return "redirect:/admin_page";
        }
        User user = repoUser.findByEmail(currentUserName);
        model2.addAttribute("user", user);
        return "home_page";
    }


    @GetMapping("/adauga_anunt")
    public String viewAdaugaAnuntPage(Model model) {
        model.addAttribute("car", new Car());
        return "adauga";
    }

    @GetMapping("/anunturile_mele")
    public String viewAnunturileMelePage(Model model) {

        List<Car> listCars = repoCar.findAll();
        List<Long> ids = new ArrayList<>();
        String currentUserName = "";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            currentUserName = authentication.getName();
        }
        for(Car car : listCars){
            if(currentUserName.equals(car.getUser().getEmail())){
                ids.add(car.getId());
            }
        }
        List<Car> listUserCars = repoCar.findAllById(ids);

        model.addAttribute("listCars",listUserCars);
        return "anunturi";
    }

    @GetMapping("/favorite")
    public String viewFavoritePage(Model model) {
        String currentUserName = "";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            currentUserName = authentication.getName();
        }
        User user = repoUser.findByEmail(currentUserName);
        model.addAttribute("listCars",user.favCars);
        return "fav";
    }

    @GetMapping("/neconectat")
    public String viewNeconectatPage() {
        return "neconectat";
    }

    @GetMapping("/sters")
    public String viewDeleted() {
        return "sters";
    }

    @PostMapping("/process_register")
    public String processRegistration(User user) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        repoUser.save(user);
        return "register_success";
    }

    @PostMapping("/process_car")
    public String processCar(Car car, @RequestParam("fileImage")MultipartFile multipartFile) throws IOException {
        String currentUserName = "";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            currentUserName = authentication.getName();
        }
        car.setUser(repoUser.findByEmail(currentUserName));
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        car.setImg(fileName);
        Car savedCar = repoCar.save(car);
        String uploadDir = "./car-img/" + savedCar.getId();
        Path uploadPath = Paths.get(uploadDir);
        if(!Files.exists(uploadPath)){
            Files.createDirectories(uploadPath);
        }

        try(InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }catch(IOException e){
                throw new IOException("Could not save uploaded file: " + fileName);
        }


        return "adauga_success";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";
        }
        return "redirect:/";
    }

    @GetMapping("/admin_page")
    public String showAdminPage() {
        return "admin_page";
    }

    @GetMapping("/list_anunturi")
    public String viewListAnunturi(Model model) {
        List<Car> listCars = repoCar.findAll();
        model.addAttribute("listCars",listCars);
        return "list_anunturi";
    }

    @GetMapping("/list_users")
    public String viewListUsers(Model model) {
        List<User> listUsers = repoUser.findAll();
        model.addAttribute("listUsers",listUsers);
        return "users";
    }

    @RequestMapping(value = "/delete_car/{id}")
    private String deleteCar(@PathVariable(name = "id") long id){
        repoCar.deleteById(id);
        return "redirect:/sters";
    }

    @RequestMapping(value = "/add_favorite/{id}")
    private String addFavorite(@PathVariable(name = "id") long id){
        String currentUserName = "";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            currentUserName = authentication.getName();
        }
        User user = repoUser.findByEmail(currentUserName);
        user.favCars.add(repoCar.findByID(id));
        repoUser.save(user);
        return "redirect:/home_page";
    }

    @RequestMapping(value = "/remove_favorite/{id}")
    private String removeFavorite(@PathVariable(name = "id") long id){
        String currentUserName = "";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            currentUserName = authentication.getName();
        }
        User user = repoUser.findByEmail(currentUserName);
        user.favCars.remove(repoCar.findByID(id));
        repoUser.save(user);
        Car car = new Car();
        return "redirect:/favorite";
    }

    @GetMapping("car/edit/{id}")
    public String showEditCarForm(@PathVariable("id") long id, Model model){
        Car car = repoCar.findByID(id);
        model.addAttribute("car",car);
        return "adauga";
    }

    @GetMapping("sort/km")
    public String sortKM(Model model1, Model model2){
        List<Car> listCars = repoCar.findAll(Sort.by("km"));
        model1.addAttribute("listCars",listCars);
        String currentUserName = "";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            currentUserName = authentication.getName();
        }
        User user = repoUser.findByEmail(currentUserName);
        model2.addAttribute("user", user);
        if (SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser"))
        {
        return "index";
        }
        return "home_page";
    }
    @GetMapping("sort/an")
    public String sortAN(Model model1, Model model2){
        List<Car> listCars = repoCar.findAll(Sort.by("an"));
        model1.addAttribute("listCars",listCars);
        String currentUserName = "";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            currentUserName = authentication.getName();
        }
        User user = repoUser.findByEmail(currentUserName);
        model2.addAttribute("user", user);
        if (SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser"))
        {
            return "index";
        }
        return "home_page";
    }
    @GetMapping("sort/pret")
    public String sortPRET(Model model1, Model model2){
        List<Car> listCars = repoCar.findAll(Sort.by("pret"));
        model1.addAttribute("listCars",listCars);
        String currentUserName = "";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            currentUserName = authentication.getName();
        }
        User user = repoUser.findByEmail(currentUserName);
        model2.addAttribute("user", user);
        if (SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser"))
        {
            return "index";
        }
        return "home_page";
    }
    @GetMapping("/search")
    public String search(@Param("keyword") String keyword, Model model1, Model model2){
        List<Car> listCars = repoCar.search(keyword);
        model1.addAttribute("listCars",listCars);
        String currentUserName = "";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            currentUserName = authentication.getName();
        }
        User user = repoUser.findByEmail(currentUserName);
        model2.addAttribute("user", user);
        if (SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser"))
        {
            return "index";
        }
        return "home_page";
    }
}

