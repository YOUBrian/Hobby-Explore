package brian.project.hobbyexplore.data

class DefaultRepository(
    private val RemoteDataSource: DataSource,
    private val LocalDataSource: DataSource,
) : Repository {

}