# ErrorControllerDemoSvc

http://127.0.0.1:5502/
http://localhost:5502/swagger-ui.html#/

https://stackoverflow.com/questions/25356781/spring-boot-remove-whitelabel-error-page


@RestController
@RequestMapping("/error")
public class SimpleErrorController implements ErrorController {

  private final ErrorAttributes errorAttributes;

  @Autowired
  public SimpleErrorController(ErrorAttributes errorAttributes) {
    Assert.notNull(errorAttributes, "ErrorAttributes must not be null");
    this.errorAttributes = errorAttributes;
  }

  @Override
  public String getErrorPath() {
    return "/error";
  }

  @RequestMapping
  public Map<String, Object> error(HttpServletRequest aRequest){
     Map<String, Object> body = getErrorAttributes(aRequest,getTraceParameter(aRequest));
     String trace = (String) body.get("trace");
     if(trace != null){
       String[] lines = trace.split("\n\t");
       body.put("trace", lines);
     }
     return body;
  }

  private boolean getTraceParameter(HttpServletRequest request) {
    String parameter = request.getParameter("trace");
    if (parameter == null) {
        return false;
    }
    return !"false".equals(parameter.toLowerCase());
  }

  private Map<String, Object> getErrorAttributes(HttpServletRequest aRequest, boolean includeStackTrace) {
    RequestAttributes requestAttributes = new ServletRequestAttributes(aRequest);
    return errorAttributes.getErrorAttributes(requestAttributes, includeStackTrace);
  }
}

