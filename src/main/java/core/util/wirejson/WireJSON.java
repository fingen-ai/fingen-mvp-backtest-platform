package core.util.wirejson;

import core.service.publisher.PublisherData;
import publish.LandingPageData;

import java.io.IOException;

public interface WireJSON {

    void wireLandingPageJSON(LandingPageData landingPageData) throws IOException;
}
