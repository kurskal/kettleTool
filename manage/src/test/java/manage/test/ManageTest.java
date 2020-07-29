package manage.test;

import manage.service.ManageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ManageTest {

    @Autowired
    ManageService manageService;

    @Test
    public void listTest(){
        List<Map<String,Object>> list = manageService.listOfKettleConfig();
        for(Map<String,Object> map : list){
            Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry<String, Object> entry = iterator.next();
                System.out.println("key is " + entry.getKey() + " , value is " + entry.getValue());
            }
            System.out.println();
        }
    }


    @Test
    public void deleteKettleConfigTest(){
        boolean result = manageService.deleteKettleConfig("testKettle");
        System.out.println(result);
        listTest();
    }

}
