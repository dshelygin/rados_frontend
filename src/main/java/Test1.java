import s3.S3Connection;


/**
 * Created by dshelygin on 09.08.2017.
 * тест
 */



public class Test1 {

    public static void main(String args[]) {

        System.out.print("Hello\n");

        try {
            //инициализация соединения с хранилищем S3
            S3Connection.INSTANCE.connect();
            //запуск web-сервера для получения запросов
            new NettyHttpServer().start();

        } catch (Exception e) {
            System.err.print(e.getMessage());
        }
    }



}
