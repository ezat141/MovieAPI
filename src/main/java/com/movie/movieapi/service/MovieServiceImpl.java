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
import java.util.List;

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
        return null;
    }

    @Override
    public List<MovieDto> getAllMovies() {
        return List.of();
    }

    @Override
    public MovieDto updateMovie(Integer movieId, MovieDto movieDto, MultipartFile file) throws IOException {
        return null;
    }

    @Override
    public String deleteMovie(Integer movieId) throws IOException {
        return "";
    }
}
