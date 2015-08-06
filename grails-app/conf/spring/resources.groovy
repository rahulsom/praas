import com.github.rahulsom.praas.Provider
import grails.rest.render.hal.HalJsonCollectionRenderer
import grails.rest.render.hal.HalJsonRenderer

// Place your Spring DSL code here
beans = {
    halNdcRenderer(HalJsonRenderer, Provider)
    halNdcListRenderer(HalJsonCollectionRenderer, Provider)

}
