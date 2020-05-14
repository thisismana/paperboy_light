package de.stroeer.paperboy_light.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.stroeer.api.v1.article.Article;
import de.stroeer.api.v1.article.ArticleServiceGrpc;
import de.stroeer.api.v1.article.GetArticleRequest;
import de.stroeer.api.v1.article.GetArticleResponse;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ArticleService extends ArticleServiceGrpc.ArticleServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(ArticleService.class);

    private final ArticleRepo repo;

    @Inject
    public ArticleService(ArticleRepo repo) {
        this.repo = repo;
    }

    @Override
    public void getArticle(GetArticleRequest request, StreamObserver<GetArticleResponse> responseObserver) {
        logger.info("getArticle({})", request.getArticleId());
        repo.byId(request.getArticleId()).whenComplete((response, throwable) -> {

                    if (throwable != null) {
                        responseObserver.onError(
                                Status.INTERNAL.withDescription(
                                        String.format("fetching %s failed", request.getArticleId()))
                                        .withCause(throwable)
                                        .asRuntimeException());
                        return;
                    }

                    if (response.item().size() == 0) {
                        // TODO: fallback to polyphase grpc and send id to sqs
                        responseObserver.onError(Status.NOT_FOUND.withDescription(
                                String.format("article %s not found", request.getArticleId()))
                                .asRuntimeException());
                        return;
                    }

                    try {
                        Article article = ArticleMapper.fromMap(response.item());
                        responseObserver.onNext(GetArticleResponse.newBuilder().setResult(article).build());
                        responseObserver.onCompleted();
                    } catch (Exception e) {
                        logger.error("mapping {} failed", request.getArticleId(), e);
                        responseObserver.onError(Status.INTERNAL.withDescription(
                                String.format("mapping %s failed", request.getArticleId()))
                                .withCause(e)
                                .asRuntimeException());
                    }
                }
        );
    }
}
