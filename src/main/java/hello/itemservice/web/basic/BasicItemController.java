package hello.itemservice.web.basic;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.PostConstruct;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/basic/items")
@RequiredArgsConstructor // 생성자 주입 알아서해줌
public class BasicItemController {

    private final ItemRepository itemRepository;

    @ModelAttribute("regions") // 어떤거든지 모델에 자동으로 addAttribute 해줌.
    public Map<String, String > regions (){
        Map<String, String> regions = new LinkedHashMap<>();

        regions.put("SEOUL","서울");
        regions.put("BUSAN","부산");
        regions.put("JEJU","제주");
        return regions;
    }

    @GetMapping
    public String items(Model model){
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items",items);
        return "basic/items";
    }
    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model){
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item",item);

        return "basic/item";
    }

    @GetMapping("/add")
    public String addForm(Model model){
        model.addAttribute("item", new Item());

        return "basic/addForm";
    }

    //@PostMapping ("/add")
    public String addItemV1(@RequestParam String itemName,
                       @RequestParam int price,
                       @RequestParam Integer quantity,
                       Model model){
        Item item = new Item();
        item.setItemName(itemName);
        item.setPrice(price);
        item.setQuantity(quantity);

        itemRepository.save(item);

        model.addAttribute("item",item);

        return "basic/item";
    }
    //@PostMapping ("/add")
    public String addItemV2(@ModelAttribute("item") Item item){ // "item" 으로 모델을 넘겨줌. 그리고 "item" 조차 생략가능. 객체 앞글자만 소문자로 바꿔서 넘겨줌.
        itemRepository.save(item);

        //model.addAttribute("item",item); 생략가능 모델어트리뷰트 떄문에

        return "basic/item";
    }

    //@PostMapping ("/add")
    public String addItemV3(@ModelAttribute("item") Item item){ // "item" 으로 모델을 넘겨줌. 그리고 "item" 조차 생략가능. 객체 앞글자만 소문자로 바꿔서 넘겨줌.
        itemRepository.save(item);

        //model.addAttribute("item",item); 생략가능 모델어트리뷰트 떄문에

        return "redirect:/basic/items/"+item.getId();
    }

    @PostMapping ("/add")
    public String addItemV4(@ModelAttribute("item") Item item, RedirectAttributes redirectAttributes){

        log.info("item.open={}",item.getOpen());
        log.info("item.regions={}", item.getRegions());

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId",savedItem.getId());
        redirectAttributes.addAttribute("status",true);

        return "redirect:/basic/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model){
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item",item);

        return "basic/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item){
        itemRepository.update(itemId, item);
        return "redirect:/basic/items/{itemId}";
    }

    /**
     * 테스트용 데이터 추가
     */
    @PostConstruct
    public void init(){
        itemRepository.save(new Item("itemA",10000,10));
        itemRepository.save(new Item("itemB",20000,20));
    }
}
