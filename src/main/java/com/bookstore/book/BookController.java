package com.bookstore.book;

import com.bookstore.user.User;
import com.bookstore.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collection;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class BookController {

    private final BookService service;
    private final UserService userService;

    @GetMapping
    public String index(Model model){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findUserByEmail(email);
        Collection<? extends GrantedAuthority> authorities =
                SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        boolean admin = authorities.contains(new SimpleGrantedAuthority("ADMIN"));

        model.addAttribute("username", user.getFirstName());
        model.addAttribute("admin", admin);

        return "index";
    }

    @GetMapping("/home")
    public String home(Model model){

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findUserByEmail(email);
        Collection<? extends GrantedAuthority> authorities =
                SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        boolean admin = authorities.contains(new SimpleGrantedAuthority("ADMIN"));

        model.addAttribute("username", user.getFirstName());
        model.addAttribute("admin", admin);

        return "home";
    }

    @GetMapping("/book-register")
    public String bookRegister(Model model){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findUserByEmail(email);
        Collection<? extends GrantedAuthority> authorities =
                SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        boolean admin = authorities.contains(new SimpleGrantedAuthority("ADMIN"));

        model.addAttribute("username", user.getFirstName());
        model.addAttribute("admin", admin);

        return "book_register";
    }

    @GetMapping("/available-books")
    public ModelAndView getAllBooks(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Collection<? extends GrantedAuthority> authorities =
                SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        boolean admin = authorities.contains(new SimpleGrantedAuthority("ADMIN"));

        User user = userService.findUserByEmail(email);
        List<Book> list = service.getAllBooks();
        List<Book> userBookList = userService.bookList(user.getId());
        List<BookDto> bookDTOs = list.stream()
                .map(book -> {
                    BookDto bookDto = new BookDto(book.getId(),book.getName(),
                            book.getAuthor(), book.getPrice(), true);
                    if(userBookList.contains(book)){
                        bookDto.setNotAdded(false);
                        return bookDto;
                    }
                    return bookDto;

                })
                .toList();

        ModelAndView mv = new ModelAndView();
        mv.setViewName("book_list");
        mv.addObject("books", bookDTOs);
        mv.addObject("username", user.getFirstName());
        mv.addObject("admin", admin);
        return mv;
    }

    @GetMapping("/my-books")
    public String myBook(Model model){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Collection<? extends GrantedAuthority> authorities =
                SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        boolean admin = authorities.contains(new SimpleGrantedAuthority("ADMIN"));

        User user = userService.findUserByEmail(email);
        List<Book> books = userService.bookList(user.getId());

        model.addAttribute("username", user.getFirstName());
        model.addAttribute("books", books);
        model.addAttribute("admin", admin);

        return "my_books";
    }

    @RequestMapping("/add-book/{id}")
    public String addToMyBook(@PathVariable("id") Integer id){
        Book book = service.getBookById(id);
        if(book != null){
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.findUserByEmail(email);
            userService.addBook(user.getId(), book);
        }

        return "redirect:/available-books";
    }

    @RequestMapping("/remove-book/{id}")
    public String removeFromMyBook(@PathVariable("id") Integer id){
        Book book = service.getBookById(id);
        if(book != null){
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.findUserByEmail(email);
            userService.removeBook(user.getId(), book);
        }

        return "redirect:/available-books";
    }

    @RequestMapping("/remove/{id}")
    public String removeMyBook(@PathVariable("id") Integer id){
        Book book = service.getBookById(id);
        if(book != null){
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.findUserByEmail(email);
            userService.removeBook(user.getId(), book);
        }
        return "redirect:/my-books";
    }

    @PostMapping("/save-book")
    public String addBook(@ModelAttribute Book book){

        service.save(book);
        return "redirect:/available-books";
    }

    //book edit
    @RequestMapping("/edit-book/{id}")
    public String editBook(@PathVariable("id") Integer id, Model model){
        Collection<? extends GrantedAuthority> authorities =
                SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        boolean admin = authorities.contains(new SimpleGrantedAuthority("ADMIN"));
        Book book = service.getBookById(id);
        if(book != null){
            model.addAttribute("book", book);
        }
        model.addAttribute("admin", admin);
        return "book_edit";
    }

    //delete books from book list
    @RequestMapping("/delete-book/{id}")
    public String deleteBook(@PathVariable Integer id){
        service.deleteBookById(id);
        return "redirect:/available-books";
    }

    //price validation when registering book
    @PostMapping("/validate-price")
    @ResponseBody
    public String validatePrice(@ModelAttribute("price") String price){
        try{
            double p = Double.parseDouble(price);
            return "<span></span>";
        } catch (Exception e){
            return
                    """
                        <span style="color: red;">Invalid Price</span>
                    """;
        }
    }

}
