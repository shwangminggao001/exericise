package com.wmg.web;

import com.wmg.dto.AppointExecution;
import com.wmg.dto.Result;
import com.wmg.entity.Book;
import com.wmg.enums.AppointStateEnum;
import com.wmg.exception.NoNumberException;
import com.wmg.exception.RepeatAppointException;
import com.wmg.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/book") // url:/模块/资源/{id}/细分 /seckill/list
public class BookController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BookService bookService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    private List<Book> list(Model model) {
        List<Book> list = bookService.getList();
        return list;
    }

    @RequestMapping(value = "/{bookId}/detail", method = RequestMethod.GET)
    @ResponseBody
    private Book detail(@PathVariable("bookId") Long bookId, Model model) {
//        if (bookId == null) {
//            return "redirect:/book/list";
//        }
        Book book = bookService.getById(bookId);
//        if (book == null) {
//            return "forward:/book/list";
//        }
//        model.addAttribute("book", book);
        return book;
    }

    // ajax json
    @RequestMapping(value = "/{bookId}/appoint", method = RequestMethod.POST, produces = {
            "application/json; charset=utf-8"})
    @ResponseBody
    private Result<AppointExecution> appoint(@PathVariable("bookId") Long bookId, @RequestParam("studentId") Long studentId) {
        if (studentId == null || studentId.equals("")) {
            return new Result<AppointExecution>(false, "学号不能为空");
        }
        AppointExecution execution = null;
        try {
            execution = bookService.appoint(bookId, studentId);
        } catch (NoNumberException e1) {
            execution = new AppointExecution(bookId, AppointStateEnum.NO_NUMBER);
        } catch (RepeatAppointException e2) {
            execution = new AppointExecution(bookId, AppointStateEnum.REPEAT_APPOINT);
        } catch (Exception e) {
            execution = new AppointExecution(bookId, AppointStateEnum.INNER_ERROR);
        }
        return new Result<AppointExecution>(true, execution);
    }

}
