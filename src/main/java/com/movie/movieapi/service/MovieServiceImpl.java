package com.movie.movieapi.service;

import com.movie.movieapi.dto.MovieDto;
import com.movie.movieapi.entities.Movie;
import com.movie.movieapi.exceptions.FileExistsException;
import com.movie.movieapi.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final FileService fileService;

    @Value("${project.poster}")
    private String path;

    @Value("${base.url}")
    private String baseUrl;



    public MovieServiceImpl(MovieRepository movieRepository, FileService fileService) {
        this.movieRepository = movieRepository;
        this.fileService = fileService;
    }
    @Override
    public MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException {
        //1. upload the file
        if(Files.exists(Paths.get(path + File.separator + file.getOriginalFilename()))){
            throw new FileExistsException("File already exists! Please enter another file name!");
        }
        String uploadedFileName = fileService.uploadFile(path, file);

        //2. set the value of field 'poster' as filename
        movieDto.setPoster(uploadedFileName);


        //3. map movieDto to movie
        Movie movie = new Movie(
          movieDto.getMovieId(),
          movieDto.getTitle(),
          movieDto.getDirector(),
          movieDto.getStudio(),
          movieDto.getMovieCast(),
          movieDto.getReleaseYear(),
          movieDto.getPoster()
        );

        //4. save movie object -> saved movie object
        Movie savedMovie = movieRepository.save(movie);

        //5. generate the posterUrl
        String posterUrl = baseUrl + "/file/" + uploadedFileName;

        //6. map movie object to Dto object and return it

        return new MovieDto(
                savedMovie.getMovieId(),
                savedMovie.getTitle(),
                savedMovie.getDirector(),
                savedMovie.getStudio(),
                savedMovie.getMovieCast(),
                savedMovie.getReleaseYear(),
                savedMovie.getPoster(),
                posterUrl
        );
    }

    @Override
    public MovieDto getMovie(Integer movieId) {
        // 1. check the data in DB and is existing, fetch the data as given ID
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with id = "+ movieId));
        // 2. generate posterUrl
        String posterUrl = baseUrl + "/file/" + movie.getPoster();

        //3. map to movie Dto object and return it


        return new MovieDto(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl
        );
    }

    @Override
    public List<MovieDto> getAllMovies() {
        //1. fetch all data from DB

        List<Movie> movies = movieRepository.findAll();
        List<MovieDto> movieDtos = new ArrayList<>();

        //2. iterate through the list generate posterUrl for each movie object
        for (Movie movie : movies) {
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            MovieDto movieDto = new MovieDto(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl
            );
            movieDtos.add(movieDto);

        }



        return movieDtos;
    }

    @Override
    public MovieDto updateMovie(Integer movieId, MovieDto movieDto, MultipartFile file) throws IOException {
        // 1. check the data in DB and is existing, fetch the data as given ID
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with id = "+ movieId));

        //2. if file is null, do nothing if file is not null, then delete existing file
        // associated with the record and upload the new file
        String fileName = movie.getPoster();
        if(file != null){
           Files.deleteIfExists(Paths.get(path + File.separator + fileName));
           fileName = fileService.uploadFile(path, file);
        }
        // 3. set movieDto's poster value, according to step2
        movieDto.setPoster(fileName);

        // 4. map it to Movie object
        Movie mappedMovie = new Movie(
                movie.getMovieId(),
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getMovieCast(),
                movieDto.getReleaseYear(),
                movieDto.getPoster()
        );

        // 5. save the movie object -> return saved movie object

        Movie updatedMovie = movieRepository.save(mappedMovie);

        // 6. generate posterUrl for it
        String posterUrl = baseUrl + "/file/" + fileName;

        // 7. map to MovieDto and return it

        return new MovieDto(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl
        );
    }

    @Override
    public String deleteMovie(Integer movieId) throws IOException {
        // 1. check if movie object exists in DB

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with id = "+ movieId));
        Integer id = movie.getMovieId();

        // 2. delete the file associated with this object
        Files.deleteIfExists(Paths.get(path + File.separator + id));

        // 3. delete the movie object
        movieRepository.delete(movie);

        return "Movie deleted with id = " + id;
    }
}
