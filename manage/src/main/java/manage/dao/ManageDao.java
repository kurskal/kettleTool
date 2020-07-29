package manage.dao;

import java.util.List;
import java.util.Map;

public interface ManageDao {

    List<Map<String,Object>> listOfKettleConfig();

    int saveKettleConfig(String kettleName, Map<String,Object> kettleConfigMap) throws Exception;

    int updateKettleName(String oldKettleName, String newKettleName) throws Exception;

    boolean deleteKettleConfig(String id);
}
