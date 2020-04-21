package com.backbase.api.simulator.spec;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Optional;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

public class ApiSpecServletTest {

    private static final String API_SPEC_CONTENT = "api-spec";

    private SpecDownloader downloader;
    private ApiSpecServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @Before
    public void beforeClass() throws IOException {
        downloader = mock(SpecDownloader.class);
        servlet = new ApiSpecServlet(downloader);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        ServletOutputStream outputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(outputStream);
    }

    @Test
    public void testSuccessfulDownload() throws IOException {
        when(downloader.download()).thenReturn(Optional.of(API_SPEC_CONTENT));
        servlet.doGet(request, response);
        verify(response).setStatus(HttpStatus.OK.value());
    }

    @Test
    public void testSpecNotFound() throws IOException {
        when(downloader.download()).thenReturn(Optional.empty());
        servlet.doGet(request, response);
        verify(response).setStatus(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void testRuntimeException() throws IOException {
        when(downloader.download()).thenThrow(new NullPointerException());
        servlet.doGet(request, response);
        verify(response).setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
