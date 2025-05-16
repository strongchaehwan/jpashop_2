package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createForm(Model model) {
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }

    @PostMapping("/items/new")
    public String create(@ModelAttribute BookForm form) {
        Book book = new Book();
        book.setName(form.getName());
        book.setIsbn(form.getIsbn());
        book.setAuthor(form.getAuthor());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());

        itemService.save(book);

        return "redirect:/items";
    }

    @GetMapping("/items")
    public String list(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }

    @GetMapping("/items/{itemId}/edit")
    public String updateItemForm(@PathVariable(name = "itemId") Long id, Model model) {
        Book item = (Book) itemService.findOne(id);

        BookForm form = new BookForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form", form);

        return "items/updateItemForm";
    }

    /**
     * 상품 수정
     * 엔티티를 변경할 때는 항상 변경 감지를 사용하세요
     * 컨트롤러에서 어설프게 엔티티를 생성하지 마세요.
     * 트랜잭션이 있는 서비스 계층에 식별자( `id` )와 변경할 데이터를 명확하게 전달하세요.
     * (파라미터 or dto) 트랜잭션이 있는 서비스 계층에서 영속 상태의 엔티티를 조회하고, 엔티티의 데이터를 직접 변경하세요.
     * 트랜잭션 커밋 시점에 변경 감지가 실행됩니다.
     */
    @PostMapping(value = "/items/{itemId}/edit")
    public String updateItem(@ModelAttribute("form") BookForm form, @PathVariable Long itemId) {
//        Book book = new Book();
//        book.setId(itemId);
//        book.setName(form.getName());
//        book.setPrice(form.getPrice());
//        book.setStockQuantity(form.getStockQuantity());
//        book.setAuthor(form.getAuthor());
//        book.setIsbn(form.getIsbn());
//        itemService.save(book);

        itemService.update(itemId, form.getName(), form.getPrice(), form.getStockQuantity());
        return "redirect:/items";
    }


}
