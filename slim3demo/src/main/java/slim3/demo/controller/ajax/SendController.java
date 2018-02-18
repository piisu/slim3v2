package slim3.demo.controller.ajax;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.util.BeanUtil;
import slim3.demo.meta.BlogMeta;
import slim3.demo.model.Blog;

public class SendController extends Controller {

    private BlogMeta b = BlogMeta.get();

    @Override
    public Navigation run() throws Exception {
        Blog blog = new Blog();
        BeanUtil.copy(request, blog);

        ObjectMapper mapper = new ObjectMapper();

        response.getWriter().write(mapper.writeValueAsString(blog));
        return null;
    }
}
