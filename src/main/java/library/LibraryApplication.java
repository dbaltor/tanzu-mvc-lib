
package library;

import library.model.BookService;
import library.model.ReaderService;

import org.springframework.context.ApplicationContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

import javax.annotation.PreDestroy;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@RequiredArgsConstructor @Slf4j
public class LibraryApplication {

    public static final String UI_CONFIG_NAME = "uiConfig";

    public static UIConfig getUIConfig(){

        String instanceIndex = System.getenv("CF_INSTANCE_INDEX");
        return instanceIndex == null ? UIConfig.of(false, 0) : UIConfig.of(true, Integer.valueOf(instanceIndex));
    }

    //@Autowired
    private final @NonNull BookService bookService;
    private final @NonNull ReaderService readerService;

    public static void main(String[] args){
        SpringApplication.run(LibraryApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) 
    {
        return args -> {
            Optional<Integer> nBooks = Optional.empty();
            Optional<Integer> nReaders = Optional.empty();
            
            // Reading command line parameters      
            // parameter key's length
            val PARAM_KEY_LENGHT = 3;
            if (args.length > 0) {
                for (String arg: args) {
                    if (arg.length() >= PARAM_KEY_LENGHT && arg.substring(0,PARAM_KEY_LENGHT).equals("-b="))
                        // get number of books
                        try {
                            nBooks = Optional.of(Integer.parseInt(arg.substring(PARAM_KEY_LENGHT)));
                        } catch (NumberFormatException nfe) {
                            log.info("Invalid format number provided! Will load the DEFAULT number of books");
                        }
                    else if (arg.length() >= PARAM_KEY_LENGHT && arg.substring(0,PARAM_KEY_LENGHT).equals("-r="))
                        // get number of readers
                        try{
                            nReaders = Optional.of(Integer.parseInt(arg.substring(PARAM_KEY_LENGHT)));
                        } catch (NumberFormatException nfe) {
                            log.info("Invalid format number provided! Will load the DEFAULT number of readers");
                        }
                    else 
                        log.info(
                            String.format("Not recognised parameter ignored: %s", arg));
                }
            }
            //val readers = readerService.loadDatabase(nReaders);
            //bookService.loadDatabase(nBooks, Optional.of(readers));
        };
    }

    @PreDestroy
    public void onExit() {
        readerService.cleanUpDatabase();
        bookService.cleanUpDatabase();
    }
}