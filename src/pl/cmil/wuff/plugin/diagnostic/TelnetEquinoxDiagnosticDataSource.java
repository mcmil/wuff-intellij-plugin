package pl.cmil.wuff.plugin.diagnostic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.telnet.TelnetClient;

/**
 * Created by mcmil on 2014-11-26.
 */
public class TelnetEquinoxDiagnosticDataSource
{
    public List< List< String >> getCommands()
    {
        List< List< String >> result = new ArrayList<>();
        TelnetClient telnet = new TelnetClient();

        try
        {

            telnet.connect( "127.0.0.1", 5555 );
            BufferedReader br = new BufferedReader( new InputStreamReader( telnet.getInputStream() ) );
            BufferedWriter bw = new BufferedWriter( new OutputStreamWriter( telnet.getOutputStream() ) );

            String s = null;
            bw.write( "ss\n" );
            bw.write( "diag\n" );
            bw.write( "finish\n" );

            bw.flush();


            result.addAll( readCommands( 3, br ).subList( 1, 3 ) );

        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                telnet.disconnect();
            }
            catch( IOException e )
            {
                e.printStackTrace();
            }

        }
        return result;
    }

    private List< List< String > > readCommands( int number, BufferedReader br ) throws IOException
    {
        String delimiter = "osgi>";
        List< List< String >> cmds = new ArrayList<>();
        List< String > current = new ArrayList<>();

        cmds.add( current );

        String s;
        int i = 0;
        while( (s = br.readLine()) != null )
        {

            if( s.startsWith( delimiter ) )
            {
                current = new ArrayList<>();
                cmds.add( current );
                i++;

                current.add( s.substring( delimiter.length() + 1 ) );
                if( i >= number )
                {
                    break;
                }

            }
            else if( !s.isEmpty() )
            {

                current.add( s );
            }
        }

        return cmds;
    }
}
